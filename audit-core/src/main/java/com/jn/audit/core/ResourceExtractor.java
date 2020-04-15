package com.jn.audit.core;

import com.jn.audit.core.model.Resource;
import com.jn.langx.util.function.Supplier;

public interface ResourceExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Resource> {
}
