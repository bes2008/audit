package com.jn.audit.spring.simple;

import com.jn.agileway.aop.adapter.aopalliance.MethodInvocationAdapter;
import com.jn.audit.core.Auditor;
import com.jn.audit.core.auditing.aop.AuditMethodInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

public class ControllerAuditInterceptor implements MethodInterceptor {

    private AuditMethodInterceptor delegate;
    private Auditor auditor;
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return delegate.intercept(new MethodInvocationAdapter(invocation));
    }

    @Autowired
    public void setDelegate(AuditMethodInterceptor delegate) {
        this.delegate = delegate;
    }

    @Autowired
    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }
}
