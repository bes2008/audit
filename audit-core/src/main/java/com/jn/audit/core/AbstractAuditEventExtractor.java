package com.jn.audit.core;

import com.jn.audit.core.model.*;
import com.jn.audit.core.operation.OperationExtractor;
import com.jn.audit.core.resource.ResourceExtractor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAuditEventExtractor<AuditedRequest, AuditedRequestContext> implements AuditEventExtractor<AuditedRequest, AuditedRequestContext> {

    protected OperationExtractor<AuditedRequest, AuditedRequestContext> operationExtractor;
    protected ResourceExtractor<AuditedRequest, AuditedRequestContext> resourceExtractor;

    @Override
    public AuditEvent get(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        AuditEvent event = new AuditEvent();
        wrappedRequest.setAuditEvent(event);
        event.setService(extractService(wrappedRequest));
        event.setPrincipal(extractPrincipal(wrappedRequest));
        event.setOperation(extractOperation(wrappedRequest));
        event.setResources(extractResources(wrappedRequest));
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
    public List<Resource> extractResources(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        if (resourceExtractor != null) {
            return resourceExtractor.get(wrappedRequest);
        }
        return new ArrayList<>();
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

    public ResourceExtractor<AuditedRequest, AuditedRequestContext> getResourceExtractor() {
        return resourceExtractor;
    }

    public void setResourceExtractor(ResourceExtractor<AuditedRequest, AuditedRequestContext> resourceExtractor) {
        this.resourceExtractor = resourceExtractor;
    }
}
