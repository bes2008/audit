package com.jn.audit.servlet;

import com.jn.audit.core.AbstractAuditEventExtractor;
import com.jn.langx.invocation.MethodInvocation;

import javax.servlet.http.HttpServletRequest;

public class ServletAuditEventExtractor extends AbstractAuditEventExtractor<HttpServletRequest, MethodInvocation> {
    public ServletAuditEventExtractor() {
        setPrincipalExtractor(new ServletAuditEventPrincipalExtractor());
        setServiceExtractor(new ServletAuditEventServiceExtractor());
        setSessionIdExtractor(new ServletAuditEventSessionIdExtractor());
    }
}
