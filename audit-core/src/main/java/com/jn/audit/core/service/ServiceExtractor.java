package com.jn.audit.core.service;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Service;
import com.jn.langx.util.function.Supplier;

public interface ServiceExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Service> {
    @Override
    Service get(AuditRequest<AuditedRequest, AuditedRequestContext> input);
}
