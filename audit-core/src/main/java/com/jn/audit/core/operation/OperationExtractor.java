package com.jn.audit.core.operation;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Operation;
import com.jn.audit.core.model.OperationDefinition;
import com.jn.langx.util.function.Supplier;

public interface OperationExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Operation> {
    OperationDefinition findOperationDefinition(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest);
}
