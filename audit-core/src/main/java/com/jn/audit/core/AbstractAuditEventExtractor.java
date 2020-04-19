package com.jn.audit.core;

import com.jn.audit.core.model.*;
import com.jn.audit.core.operation.OperationExtractor;

public abstract class AbstractAuditEventExtractor<AuditedRequest, AuditedRequestContext> implements AuditEventExtractor<AuditedRequest, AuditedRequestContext> {

    protected OperationExtractor<AuditedRequest, AuditedRequestContext> operationExtractor;

    @Override
    public AuditEvent get(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        AuditEvent event = new AuditEvent();
        wrappedRequest.setAuditEvent(event);
        event.setService(extractService(wrappedRequest));
        event.setPrincipal(extractPrincipal(wrappedRequest));
        event.setOperation(extractOperation(wrappedRequest));
        event.setResource(extractResource(wrappedRequest));
        return event;
    }

    @Override
    public Service extractService(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        return new Service();
    }

    @Override
    public Principal extractPrincipal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        return new Principal();
    }

    @Override
    public Resource extractResource(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        return new Resource();
    }

    @Override
    public Operation extractOperation(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        return operationExtractor.get(wrappedRequest);
    }

    public OperationExtractor<AuditedRequest, AuditedRequestContext> getOperationExtractor() {
        return operationExtractor;
    }

    public void setOperationExtractor(OperationExtractor<AuditedRequest, AuditedRequestContext> operationExtractor) {
        this.operationExtractor = operationExtractor;
    }
}
