package com.jn.audit.core.resource;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.*;
import com.jn.audit.core.resource.parser.CustomNamedEntityResourceSupplierParser;
import com.jn.audit.core.resource.parser.CustomNamedMapParameterResourceSupplierParser;
import com.jn.audit.core.resource.parser.CustomResourcePropertyParameterResourceSupplierParser;
import com.jn.audit.core.resource.valuegetter.ArrayValueGetter;
import com.jn.audit.core.resource.valuegetter.PipelineValueGetter;
import com.jn.audit.core.resource.valuegetter.StreamValueGetter;
import com.jn.audit.core.resource.valuegetter.ValueGetter;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Primitives;
import com.jn.langx.util.reflect.type.Types;

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
public class ResourceMethodExtractor<AuditedRequest> implements ResourceExtractor<AuditedRequest, Method> {

    @Override
    public List<Resource> get(AuditRequest<AuditedRequest, Method> wrappedRequest) {
        final Method method = wrappedRequest.getRequestContext();
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

        Parameter[] parameters = method.getParameters();
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

        // step 2：如果 step 1 没找到，根据 resource definition 去解析 生成 supplier
        ResourceDefinition resourceDefinition = operationDefinition.getResource();
        ValueGetter resourceGetter = null;
        if (resourceDefinition != null) {
            Map<String, String> mapping = resourceDefinition;
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
                if (supplier == null) {
                    supplier = new CustomResourcePropertyParameterResourceSupplierParser(mapping).parse(parameters);
                }
                resourceGetter = supplier;
            }
        }
        // step 3: 如果 step 2 没找到，根据 注解去解析 生成 supplier

        // step 4: 如果 step 3 没找到， null

        // 如果抽取出 resource对象，但每一个 resource 对象只有 id, 没有name，则会调用 BaseIdResourceExtractor
        return null;
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
}
