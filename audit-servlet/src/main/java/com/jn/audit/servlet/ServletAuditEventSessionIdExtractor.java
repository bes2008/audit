package com.jn.audit.servlet;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.session.SessionIdExtractor;
import com.jn.langx.invocation.MethodInvocation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServletAuditEventSessionIdExtractor implements SessionIdExtractor<HttpServletRequest, MethodInvocation> {
    @Override
    public String get(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            return session.getId();
        }
        return null;
    }
}
