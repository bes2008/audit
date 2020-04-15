package com.jn.audit.core;

import com.jn.langx.Filter;

public interface AuditRequestFilter<AuditedRequest,AuditedRequestContext> extends Filter<AuditRequest<AuditedRequest,AuditedRequestContext>> {
}
