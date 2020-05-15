package com.jn.audit.core.resource;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.annotation.ResourceId;
import com.jn.audit.core.model.*;
import com.jn.audit.core.resource.parser.clazz.CustomNamedEntityResourceSupplierParser;
import com.jn.audit.core.resource.parser.parameter.*;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.proxy.aop.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objects;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.ConcurrentReferenceHashMap;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.reflect.Parameter;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.parameter.MethodParameter;
import com.jn.langx.util.reflect.reference.ReferenceType;
import com.jn.langx.util.reflect.type.Types;
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Holder;
import com.jn.langx.util.valuegetter.ArrayValueGetter;
import com.jn.langx.util.valuegetter.PipelineValueGetter;
import com.jn.langx.util.valuegetter.StreamValueGetter;
import com.jn.langx.util.valuegetter.ValueGetter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 提供了基于正在执行的方法的ResourceExtractor
 * 必须在 operation extractor 之后执行才有意义
 *
 * @param <AuditedRequest>
 * @since audit 1.0.3+, jdk 1.8+
 */
public class ResourceMethodInvocationExtractor<AuditedRequest> implements ResourceExtractor<AuditedRequest, MethodInvocation> {
    /**
     * 根据注解解析后的进行缓存
     * key: 在执行的方法
     * value: 基于方法参数的 ValueGetter
     */
    private ConcurrentReferenceHashMap<Method, Holder<ValueGetter>> annotatedCache = new ConcurrentReferenceHashMap<Method, Holder<ValueGetter>>(1000, 0.9f, Runtime.getRuntime().availableProcessors(), ReferenceType.SOFT, ReferenceType.SOFT);
    /**
     * 根据配置文件定义解析之后的缓存
     * <p>
     * key: 在执行的方法
     * value: 基于方法参数的 ValueGetter
     */
    private ConcurrentReferenceHashMap<Method, Entry<ResourceDefinition, ValueGetter>> configuredResourceCache = new ConcurrentReferenceHashMap<Method, Entry<ResourceDefinition, ValueGetter>>(1000, 0.9f, Runtime.getRuntime().availableProcessors(), ReferenceType.SOFT, ReferenceType.SOFT);

    private AbstractIdResourceExtractor idResourceExtractor;


    @Override
    public List<Resource> get(AuditRequest<AuditedRequest, MethodInvocation> wrappedRequest) {
        final MethodInvocation methodInvocation = wrappedRequest.getRequestContext();
        Method method = methodInvocation.getJoinPoint();

        AuditEvent auditEvent = wrappedRequest.getAuditEvent();

        Operation operation = auditEvent.getOperation();
        if (operation == null) {
            return null;
        }
        OperationDefinition operationDefinition = operation.getDefinition();
        if (operationDefinition == null) {
            return null;
        }

        if (Emptys.isNotEmpty(auditEvent.getResources())) {
            return auditEvent.getResources();
        }

        // step find supplier and extract resource

        // step 1：根据 method 从 cache 里找到对应的 supplier
        ResourceDefinition resourceDefinition = operationDefinition.getResourceDefinition();
        ValueGetter resourceGetter = getValueGetterFromCache(method, resourceDefinition);
        if (resourceGetter == null) {
            // step 2：如果 step 1 没找到，根据 resource definition 去解析 生成 supplier
            Parameter[] parameters = Collects.toArray(Reflects.getMethodParameters(method), MethodParameter[].class);
            resourceGetter = parseResourceGetterByConfiguration(parameters, resourceDefinition);
            if (resourceGetter != null) {
                configuredResourceCache.put(method, new Entry<ResourceDefinition, ValueGetter>(resourceDefinition, resourceGetter));
            }
            // step 3: 如果 step 2 没找到，根据 注解去解析 生成 supplier
            if (resourceGetter == null && !annotatedCache.containsKey(method)) {
                resourceGetter = parseResourceGetterByAnnotation(parameters);
                annotatedCache.putIfAbsent(method, new Holder<ValueGetter>(resourceGetter));
            }
        }
        // step 4: 如果 step 3 没找到， null
        if (resourceGetter == null) {
            return null;
        }
        List<Resource> resources = Collects.asList(Collects.asIterable(resourceGetter.get(methodInvocation.getArguments())));

        // 通常应该在 数据访问层执行下面的代码，例如mybatis 通用的 service 层
        if (idResourceExtractor != null) {
            if (Collects.allMatch(resources, new Predicate<Resource>() {
                @Override
                public boolean test(Resource value) {
                    return ResourceUtils.isOnlyResourceId(value);
                }
            })) {
                List<Serializable> ids = Pipeline.of(resources).map(new Function<Resource, Serializable>() {
                    @Override
                    public Serializable apply(Resource resource) {
                        return resource.getResourceId();
                    }
                }).asList();

                // 通常应该在 数据访问层执行下面的代码，例如mybatis 通用的 service 层
                List<Resource> resourceList = idResourceExtractor.findResources(wrappedRequest.getRequest(), ids);
                resourceList = Pipeline.of(resourceList).filter(new Predicate<Resource>() {
                    @Override
                    public boolean test(Resource resource) {
                        return ResourceUtils.isValid(resource);
                    }
                }).asList();
                return resourceList;
            }
        }
        return resources;
    }

    private ValueGetter getValueGetterFromCache(@NonNull Method method, @Nullable ResourceDefinition resourceDefinition) {
        Entry<ResourceDefinition, ValueGetter> entry = configuredResourceCache.get(method);
        if (entry != null) {
            if (Objects.equals(entry.getKey(), resourceDefinition)) {
                return entry.getValue();
            }
        }
        if (resourceDefinition == ResourceDefinition.DEFAULT_DEFINITION) {
            Holder<ValueGetter> holder = annotatedCache.get(method);
            if (holder == null) {
                return null;
            }
            return holder.get();
        }
        return null;
    }

    private ValueGetter parseResourceGetterByConfiguration(Parameter[] parameters, ResourceDefinition resourceDefinition) {
        ValueGetter resourceGetter = null;

        if (resourceDefinition != null) {
            Map<String, Parameter> parameterMap = Pipeline.of(parameters)
                    .collect(Collects.toHashMap(
                            new Function<Parameter, String>() {
                                @Override
                                public String apply(Parameter parameter) {
                                    return parameter.getName();
                                }
                            }, new Function<Parameter, Parameter>() {
                                @Override
                                public Parameter apply(Parameter parameter) {
                                    return parameter;
                                }
                            }, true));

            Map<String, Object> mapping = resourceDefinition;
            // step 2.1 : parse key: resource
            String resourceKey = resourceDefinition.getResource();
            if (Emptys.isNotEmpty(resourceKey)) {
                Parameter parameter = parameterMap.get(resourceKey);

                ResourceSupplier supplier = null;
                if (parameter != null) {
                    Class parameterType = parameter.getType();
                    Class parameterType0 = parameterType;
                    if (Types.isArray(parameterType)) {
                        parameterType0 = parameterType.getComponentType();
                    } else if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                        try {
                            parameterType0 = Types.getRawType(parameterType);
                        } catch (Throwable ex) {
                            parameterType0 = parameterType;
                        }
                    }

                    if (Reflects.isSubClassOrEquals(Map.class, parameterType0)) {
                        supplier = new CustomNamedMapParameterResourceSupplierParser(mapping).parse(parameter);
                    } else if (!Types.isLiteralType(parameterType0)) {
                        supplier = new CustomNamedEntityResourceSupplierParser(mapping).parse(parameterType0);
                    }

                    if (supplier != null) {
                        int index = Collects.firstOccurrence(Collects.asList(parameters), parameter);
                        PipelineValueGetter pipelineValueGetter = new PipelineValueGetter();
                        pipelineValueGetter.addValueGetter(new ArrayValueGetter(index));
                        if (parameterType0 == parameterType) {
                            pipelineValueGetter.addValueGetter(supplier);
                        } else {
                            pipelineValueGetter.addValueGetter(new StreamValueGetter(supplier));
                        }
                        resourceGetter = pipelineValueGetter;
                    }
                }
                // step 2.2 parse by resourceId, resourceName, resourceType
                if (supplier == null) {
                    supplier = new CustomResourcePropertyParameterResourceSupplierParser(mapping).parse(parameters);
                }
                resourceGetter = supplier;
            }
        }
        return resourceGetter;
    }

    private ValueGetter parseResourceGetterByAnnotation(final Parameter[] parameters) {
        final Holder<ValueGetter> resourceGetter = new Holder<ValueGetter>();
        // step 1: 解析 @Resource 注解
        Collects.forEach(parameters,
                new Predicate2<Integer, Parameter>() {
                    @Override
                    public boolean test(Integer key, Parameter parameter) {
                        return Reflects.hasAnnotation(parameter, com.jn.audit.core.annotation.Resource.class);
                    }
                },
                new Consumer2<Integer, Parameter>() {
                    @Override
                    public void accept(Integer index, Parameter parameter) {
                        ValueGetter supplier = null;
                        Class parameterType = parameter.getType();
                        Class parameterType0 = parameterType;
                        boolean isArray = false;
                        boolean isCollection = false;
                        if (Types.isArray(parameterType)) {
                            parameterType0 = parameterType.getComponentType();
                            isArray = true;
                        } else if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                            try {
                                parameterType0 = Types.getRawType(parameterType);
                                isCollection = true;
                            } catch (Throwable ex) {
                                parameterType0 = parameterType;
                            }
                        }

                        // 对泛型 raw type进行处理
                        // 也就是这里是支持： Collection<Map>, Collection<Entity>, Map, Entity的方式，不对 Collection<Collection>的方式做支持
                        if (Reflects.isSubClassOrEquals(Map.class, parameterType0)) {
                            supplier = new ResourceAnnotatedMapParameterResourceSupplierParser().parse(parameter);
                        } else if (!Types.isLiteralType(parameterType0)) {
                            supplier = new ResourceAnnotatedEntityParameterResourceSupplierParser().parse(parameter);
                        }

                        if (supplier != null) {
                            PipelineValueGetter pipelineValueGetter = new PipelineValueGetter();
                            // 相当于调用 parameters[index]
                            pipelineValueGetter.addValueGetter(new ArrayValueGetter(index));

                            if (!isArray && !isCollection) {
                                // 此时为 Map 或者 Entity
                                pipelineValueGetter.addValueGetter(supplier);
                            } else {
                                // 此时 为 array, collection
                                pipelineValueGetter.addValueGetter(new StreamValueGetter(supplier));
                            }
                            resourceGetter.set(pipelineValueGetter);
                        }
                    }
                },
                new Predicate2<Integer, Parameter>() {
                    @Override
                    public boolean test(Integer key, Parameter value) {
                        return !resourceGetter.isNull();
                    }
                }
        );

        // step 2: 解析 @ResourceId, @ResourceName, @ResourceType 注解
        // 这一步只针对字面量类型的解析
        if (resourceGetter.isNull()) {
            resourceGetter.set(new ResourcePropertyAnnotatedResourceSupplierParser().parse(parameters));
        }
        // step 3: 解析 Collection ids
        // 这一步只针对 id 是Collection或者Array， 并且有 @ResourceId 标注的情况
        if (resourceGetter.isNull()) {

            Collects.forEach(parameters,
                    new Predicate2<Integer, Parameter>() {
                        @Override
                        public boolean test(Integer key, Parameter parameter) {
                            return Reflects.hasAnnotation(parameter, ResourceId.class);
                        }
                    },
                    new Consumer2<Integer, Parameter>() {
                        @Override
                        public void accept(Integer index, Parameter parameter) {
                            ValueGetter supplier = null;
                            Class parameterType = parameter.getType();
                            Class parameterType0 = parameterType;
                            boolean isArray = false;
                            boolean isCollection = false;
                            if (Types.isArray(parameterType)) {
                                parameterType0 = parameterType.getComponentType();
                                isArray = true;
                            } else if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                                try {
                                    parameterType0 = Types.getRawType(parameterType);
                                    isCollection = true;
                                } catch (Throwable ex) {
                                    parameterType0 = parameterType;
                                }
                            }
                            if (isArray || isCollection) {
                                PipelineValueGetter pipelineValueGetter = new PipelineValueGetter();
                                // 相当于调用 parameters[index]
                                pipelineValueGetter.addValueGetter(new ArrayValueGetter(index));

                                pipelineValueGetter.addValueGetter(new StreamValueGetter(new Function() {
                                            @Override
                                            public Object apply(Object id) {
                                                Resource resource = new Resource();
                                                if (id == null) {
                                                    return null;
                                                }
                                                resource.setResourceId(id.toString());
                                                return resource;
                                            }
                                        })
                                );

                                resourceGetter.set(pipelineValueGetter);
                            }
                        }
                    },
                    new Predicate2<Integer, Parameter>() {
                        @Override
                        public boolean test(Integer key, Parameter value) {
                            return !resourceGetter.isNull();
                        }
                    }
            );
        }
        return resourceGetter.get();
    }


    public AbstractIdResourceExtractor getIdResourceExtractor() {
        return idResourceExtractor;
    }

    public void setIdResourceExtractor(AbstractIdResourceExtractor idResourceExtractor) {
        this.idResourceExtractor = idResourceExtractor;
    }
}
