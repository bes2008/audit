package com.jn.audit.core;

import com.jn.audit.core.model.*;

public abstract class AbstractAuditEventExtractor<AuditedRequest, AuditedRequestContext> implements AuditEventExtractor<AuditedRequest, AuditedRequestContext> {

    @Override
    public AuditEvent get(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        Service service = new Service();
        Resource resource = new Resource();
        Operation operation = new Operation();
        Principal principal = new Principal();
        AuditEvent event = new AuditEventBuilder().principal(principal).service(service).resource(resource).operation(operation).build();
        extractPrincipal(wrappedRequest, event, principal);
        extractOperation(wrappedRequest, event, operation);
        extractService(wrappedRequest, event, service);
        extractResource(wrappedRequest, event, resource);
        return event;
    }
}
