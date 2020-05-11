package com.jn.audit.core.resource;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.*;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @param <AuditedRequest>
 * @since audit 1.0.3+, jdk 1.8+
 */
public class ResourceMethodExtractor<AuditedRequest> implements ResourceExtractor<AuditedRequest, Method> {
    @Override
    public List<Resource> get(AuditRequest<AuditedRequest, Method> wrappedRequest) {
        final Method method = wrappedRequest.getRequestContext();
        AuditEvent auditEvent = wrappedRequest.getAuditEvent();
        if (Emptys.isNotEmpty(auditEvent.getResources())) {
            return auditEvent.getResources();
        }
        Operation operation = auditEvent.getOperation();
        if (operation == null) {
            return null;
        }
        OperationDefinition operationDefinition = operation.getDefinition();
        ResourceDefinition resourceDefinition = operationDefinition.getResource();


        Parameter[] parameters = method.getParameters();
        Collects.forEach(parameters, new Consumer2<Integer, Parameter>() {
            @Override
            public void accept(Integer index, Parameter parameter) {
                parameter.getAnnotations();
            }
        });

        return null;
    }
}
