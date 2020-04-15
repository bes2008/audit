package com.jn.audit.core;

import com.jn.audit.core.model.Service;
import com.jn.langx.util.function.Supplier;

public interface ServiceExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Service> {
}
