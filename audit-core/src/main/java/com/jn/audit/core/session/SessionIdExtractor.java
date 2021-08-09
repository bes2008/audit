package com.jn.audit.core.session;

import com.jn.audit.core.AuditRequest;
import com.jn.langx.util.function.Supplier;

public interface SessionIdExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, String> {
}
