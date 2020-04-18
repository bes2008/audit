package com.jn.audit.core.operation.method;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.OperationExtractor;
import com.jn.audit.core.model.Operation;
import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.operation.OperationIdGenerator;
import com.jn.audit.core.operation.OperationDefinitionParserRegistry;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 根据method查找operation definition的步骤：
 * 1. get operation definition from cache
 * 2. parse method
 * 2.1) parse annotations based on parser registry
 * 2.2) parse from configuration file
 * 2.2.1) using custom operation name generator
 * 2.2.2) using method full name (exclude parameters)
 *
 * @param <AuditedRequest>
 */
public class MethodBasedOperationExtractor<AuditedRequest> implements OperationExtractor<AuditedRequest, Method> {
    private Cache<Method, OperationDefinition> methodOperationDefinitionCache = CacheBuilder.<Method, OperationDefinition>newBuilder()
            .initialCapacity(100)
            .capacityHeightWater(0.75f)
            .softKey(true)
            .build();

    private OperationDefinitionParserRegistry operationParserRegistry;

    private OperationIdGenerator<AuditedRequest, Method> operationIdGenerator;

    @Override
    public Operation get(AuditRequest<AuditedRequest, Method> wrappedRequest) {
        Operation operation = new Operation();
        final Method method = wrappedRequest.getRequestContext();
        // step 1: get operation definition from cache
        OperationDefinition operationDefinition = methodOperationDefinitionCache.get(method);

        if (operationDefinition == null) {
            // step 2: parse method
            // step 2.1 parse annotations based on parser registry
            OperationMethodAnnotationParser<?> annotationParser = Collects.findFirst(operationParserRegistry.getAnnotationParsers(), new Predicate<OperationMethodAnnotationParser<? extends Annotation>>() {
                @Override
                public boolean test(OperationMethodAnnotationParser<? extends Annotation> parser) {
                    return Reflects.getAnnotation(method, parser.getAnnotation()) != null;
                }
            });

            if (annotationParser != null) {
                operationDefinition = annotationParser.parse(method);
            }

            if (operationDefinition == null) {
                // 2.2) parse from configuration file

            }
        }
        return null;
    }

    public Cache<Method, OperationDefinition> getMethodOperationDefinitionCache() {
        return methodOperationDefinitionCache;
    }

    public void setMethodOperationDefinitionCache(Cache<Method, OperationDefinition> methodOperationDefinitionCache) {
        this.methodOperationDefinitionCache = methodOperationDefinitionCache;
    }

    public OperationDefinitionParserRegistry getOperationParserRegistry() {
        return operationParserRegistry;
    }

    public void setOperationParserRegistry(OperationDefinitionParserRegistry operationParserRegistry) {
        this.operationParserRegistry = operationParserRegistry;
    }

    public OperationIdGenerator<AuditedRequest, Method> getOperationIdGenerator() {
        return operationIdGenerator;
    }

    public void setOperationIdGenerator(OperationIdGenerator<AuditedRequest, Method> operationIdGenerator) {
        this.operationIdGenerator = operationIdGenerator;
    }
}
