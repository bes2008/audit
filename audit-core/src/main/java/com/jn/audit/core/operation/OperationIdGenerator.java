package com.jn.audit.core.operation;

import com.jn.audit.core.AuditRequest;
import com.jn.langx.IdGenerator;

public interface OperationIdGenerator<AuditedRequest, AuditedRequestContext> extends IdGenerator<AuditRequest<AuditedRequest, AuditedRequestContext>> {

}
