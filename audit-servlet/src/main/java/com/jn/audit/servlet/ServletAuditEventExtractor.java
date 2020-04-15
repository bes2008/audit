package com.jn.audit.servlet;

import com.jn.audit.core.AuditEventExtractor;
import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.AuditEvent;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletAuditEventExtractor implements AuditEventExtractor<HttpServletRequest, Method> {
    @Override
    public AuditEvent get(AuditRequest<HttpServletRequest, Method> wrappedRequest) {
        return null;
    }
}
