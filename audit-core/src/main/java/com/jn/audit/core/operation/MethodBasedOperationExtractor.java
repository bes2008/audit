package com.jn.audit.core.operation;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.OperationExtractor;
import com.jn.audit.core.model.Operation;
import com.jn.audit.core.model.OperationDefinition;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;

import java.lang.reflect.Method;

/**
 * 根据method查找operation definition的步骤：
 * 1. get operation definition from cache
 * 2. parse method
 *      2.1) parse annotations based on parser registry
 *      2.2) parse from configuration file
 *          2.2.1) using custom operation name generator
 *          2.2.2) using method full name (exclude parameters)
 *
 * @param <AuditedRequest>
 */
public class MethodBasedOperationExtractor<AuditedRequest> implements OperationExtractor<AuditedRequest, Method> {
    private Cache<Method, OperationDefinition> methodOperationDefinitionCache = CacheBuilder.<Method, OperationDefinition>newBuilder()
            .initialCapacity(100)
            .capacityHeightWater(0.75f)
            .softKey(true)
            .build();

    private OperationAnnotationParserRegistry annotationParserRegistry = new OperationAnnotationParserRegistry();

    @Override
    public Operation get(AuditRequest<AuditedRequest, Method> wrappedRequest) {
        Operation operation = new Operation();
        Method method = wrappedRequest.getRequestContext();
        // step 1: get operation definition from cache
        OperationDefinition operationDefinition = methodOperationDefinitionCache.get(method);

        if (operationDefinition == null) {
            // step 2: parse method

        }
        return null;
    }


}
