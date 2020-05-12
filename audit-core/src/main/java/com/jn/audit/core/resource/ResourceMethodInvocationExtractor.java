package com.jn.audit.core.resource;

import com.jn.audit.core.AuditRequest;
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
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.reference.ReferenceType;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.langx.util.reflect.type.Types;
import com.jn.langx.util.struct.Entry;
import com.jn.langx.util.struct.Holder;
import com.jn.langx.util.valuegetter.ArrayValueGetter;
import com.jn.langx.util.valuegetter.PipelineValueGetter;
import com.jn.langx.util.valuegetter.StreamValueGetter;
import com.jn.langx.util.valuegetter.ValueGetter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 必须在 operation extractor 之后执行才有意义
 *
 * @param <AuditedRequest>
 * @since audit 1.0.3+, jdk 1.8+
 */
public class ResourceMethodInvocationExtractor<AuditedRequest> implements ResourceExtractor<AuditedRequest, MethodInvocation> {
    /**
     * 根据注解解析后的进行缓存
     */
    private ConcurrentReferenceHashMap<Method, Holder<ValueGetter>> annotatedCache = new ConcurrentReferenceHashMap<Method, Holder<ValueGetter>>(1000, 0.9f, Runtime.getRuntime().availableProcessors(), ReferenceType.SOFT, ReferenceType.SOFT);
    /**
     * 根据配置文件定义解析之后的缓存
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
            Parameter[] parameters = method.getParameters();
            resourceGetter = parseResourceGetterByConfiguration(parameters, resourceDefinition);
            if (resourceGetter != null) {
                configuredResourceCache.put(method, new Entry<ResourceDefinition, ValueGetter>(resourceDefinition, resourceGetter));
            }
            // step 3: 如果 step 2 没找到，根据 注解去解析 生成 supplier
            if (resourceGetter == null && !annotatedCache.containsKey(method)) {
                resourceGetter = parseResourceGetterByAnnotation(parameters);
                annotatedCache.putIfAbsent(method, new Holder<>(resourceGetter));
            }
        }
        // step 4: 如果 step 3 没找到， null
        if (resourceGetter == null) {
            return null;
        }
        Object resourcesObj = resourceGetter.get(Collects.asIterable(methodInvocation.getArguments()));
        List<Object> resourcesCollection = null;
        if (resourcesObj instanceof Resource) {
            resourcesCollection = Collects.newArrayList(resourcesObj);
        } else if (resourcesObj instanceof List) {
            resourcesCollection = (List) resourcesObj;
        }
        List<Resource> resources = Pipeline.<Object>of(resourcesCollection)
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object resource) {
                        return resource instanceof Resource;
                    }
                }).map(new Function<Object, Resource>() {
                    @Override
                    public Resource apply(Object input) {
                        return (Resource) input;
                    }
                })
                .asList();

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
                    }
                    if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                        try {
                            parameterType0 = Types.getRawType(parameterType);
                        } catch (Throwable ex) {
                            parameterType0 = parameterType;
                        }
                    }
                    if (Reflects.isSubClassOrEquals(Map.class, parameterType0)) {
                        supplier = new CustomNamedMapParameterResourceSupplierParser(mapping).parse(parameter);
                    }
                    if (!isLiteralType(parameterType)) {
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
                // step 2.2 parse by resourceId, resourceName, resourceValue
                if (supplier == null) {
                    supplier = new CustomResourcePropertyParameterResourceSupplierParser(mapping).parse(parameters);
                }
                resourceGetter = supplier;
            }
        }
        return resourceGetter;
    }

    private ValueGetter parseResourceGetterByAnnotation(final Parameter[] parameters) {
        Holder<ValueGetter> resourceGetter = new Holder<ValueGetter>();
        // step 1: 解析 @Resource 注解
        Collects.forEach(parameters, new Consumer2<Integer, Parameter>() {
            @Override
            public void accept(Integer index, Parameter parameter) {
                ResourceSupplier supplier = null;
                Class parameterType = parameter.getType();
                Class parameterType0 = parameterType;
                if (Types.isArray(parameterType)) {
                    parameterType0 = parameterType.getComponentType();
                }
                if (Reflects.isSubClassOrEquals(Collection.class, parameterType)) {
                    try {
                        parameterType0 = Types.getRawType(parameterType);
                    } catch (Throwable ex) {
                        parameterType0 = parameterType;
                    }
                }
                if (Reflects.isSubClassOrEquals(Map.class, parameterType0)) {
                    supplier = new ResourceAnnotatedMapParameterResourceSupplierParser().parse(parameter);
                }
                if (!isLiteralType(parameterType)) {
                    supplier = new ResourceAnnotatedEntityParameterResourceSupplierParser().parse(parameter);
                }

                if (supplier != null) {
                    PipelineValueGetter pipelineValueGetter = new PipelineValueGetter();
                    pipelineValueGetter.addValueGetter(new ArrayValueGetter(index));
                    if (parameterType0 == parameterType) {
                        pipelineValueGetter.addValueGetter(supplier);
                    } else {
                        pipelineValueGetter.addValueGetter(new StreamValueGetter(supplier));
                    }
                    resourceGetter.set(pipelineValueGetter);
                }
            }
        });

        // step 2: 解析 @ResourceId, @ResourceName, @ResourceType 注解
        if (resourceGetter.isNull()) {
            resourceGetter.set(new ResourcePropertyAnnotatedResourceSupplierParser().parse(parameters));
        }

        return resourceGetter.get();
    }

    private static final boolean isLiteralType(Class clazz) {
        if (Types.isPrimitive(clazz)) {
            return true;
        }
        if (Primitives.isWrapperType(clazz)) {
            return true;
        }
        if (clazz == String.class) {
            return true;
        }
        return false;
    }

    public AbstractIdResourceExtractor getIdResourceExtractor() {
        return idResourceExtractor;
    }

    public void setIdResourceExtractor(AbstractIdResourceExtractor idResourceExtractor) {
        this.idResourceExtractor = idResourceExtractor;
    }
}
