package com.jn.audit.core.filter;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.AuditRequestFilter;
import com.jn.audit.core.annotation.Audit;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;

/**
 * 由方法上的 @Audit 注解来决定该方法执行是否要被审计
 * @param <AuditedRequest>
 */
public class MethodAuditAnnotationFilter<AuditedRequest> implements AuditRequestFilter<AuditedRequest, Method> {
    private boolean auditIfAnnotationNotExists = true;

    @Override
    public boolean accept(AuditRequest<AuditedRequest, Method> wrappedRequest) {
        Method method = wrappedRequest.getRequestContext();
        Audit audit = Reflects.getAnnotation(method, Audit.class);
        if (audit == null) {
            return auditIfAnnotationNotExists;
        }
        return audit.enable();
    }

    public boolean isAuditIfAnnotationNotExists() {
        return auditIfAnnotationNotExists;
    }

    public void setAuditIfAnnotationNotExists(boolean auditIfAnnotationNotExists) {
        this.auditIfAnnotationNotExists = auditIfAnnotationNotExists;
    }
}
