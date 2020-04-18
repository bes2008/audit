package com.jn.audit.core.operation.method;

import com.jn.audit.core.operation.OperationIdGenerator;

import java.lang.reflect.Method;

public interface OperationMethodIdGenerator<AuditedRequest> extends OperationIdGenerator<AuditedRequest, Method> {
}
