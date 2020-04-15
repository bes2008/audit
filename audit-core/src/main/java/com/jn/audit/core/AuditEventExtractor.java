package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;
import com.jn.langx.util.function.Supplier;

public interface AuditEventExtractor<AuditedRequest,AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest,AuditedRequestContext>, AuditEvent> {

}
