package com.jn.audit.servlet;

import com.jn.audit.core.AbstractAuditEventExtractor;
import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Principal;
import com.jn.audit.core.model.PrincipalType;
import com.jn.audit.core.model.Service;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Strings;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletAuditEventExtractor extends AbstractAuditEventExtractor<HttpServletRequest, MethodInvocation> {

    @Override
    public Service extractService(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        Service service = new Service();
        service.setServicePort(request.getLocalPort());
        service.setServiceIp(request.getLocalAddr());
        service.setServiceProtocol(request.getProtocol());
        String context = request.getServletContext().getServletContextName();
        if (Strings.isNotEmpty(context)) {
            context = request.getContextPath();
            if (context.startsWith("/")) {
                context = context.substring(1);
            }
        }
        service.setServiceName(context);
        return service;
    }

    @Override
    public Principal extractPrincipal(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        Principal principal = new Principal();
        principal.setClientHost(request.getRemoteHost());
        principal.setClientIp(request.getRemoteAddr());
        principal.setClientPort(request.getRemotePort());
        java.security.Principal p = request.getUserPrincipal();
        String principalName = null;
        if (p != null) {
            principalName = p.getName();
        } else {
            principalName = request.getRemoteUser();
        }
        principal.setPrincipalId(request.getRemoteUser());
        principal.setPrincipalName(principalName);
        principal.setPrincipalType((Strings.isEmpty(request.getAuthType()) ? PrincipalType.anonymous : PrincipalType.authenticated).name());
        return principal;
    }


}
