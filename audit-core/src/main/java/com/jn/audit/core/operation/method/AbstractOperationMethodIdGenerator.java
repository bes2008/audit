package com.jn.audit.core.operation.method;

import com.jn.audit.core.operation.OperationIdGenerator;

import java.lang.reflect.Method;

/**
 * 方法ID生成器
 * @param <AuditedRequest>
 */
public abstract class AbstractOperationMethodIdGenerator<AuditedRequest> implements OperationIdGenerator<AuditedRequest, Method> {
    @Override
    public final String get() {
        throw new UnsupportedOperationException();
    }
}
