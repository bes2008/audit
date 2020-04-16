package com.jn.audit.core;

import com.jn.audit.core.model.*;
import com.jn.langx.util.function.Supplier;

public interface AuditEventExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, AuditEvent> {

    void extractService(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest, AuditEvent event, Service service);

    void extractPrincipal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest, AuditEvent event, Principal principal);

    void extractResource(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest, AuditEvent event, Resource resource);

    void extractOperation(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest, AuditEvent event, Operation operation);

}
