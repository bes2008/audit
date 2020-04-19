package com.jn.audit.servlet;

import com.jn.audit.core.AbstractAuditEventExtractor;
import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Principal;
import com.jn.audit.core.model.PrincipalType;
import com.jn.audit.core.model.Service;
import com.jn.langx.util.Strings;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletAuditEventExtractor extends AbstractAuditEventExtractor<HttpServletRequest, Method> {

    @Override
    public Service extractService(AuditRequest<HttpServletRequest, Method> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        Service service = new Service();
        service.setServicePort(request.getLocalPort());
        service.setServiceIp(request.getLocalAddr());
        service.setServiceProtocol(request.getProtocol());
        String context = request.getServletContext().getServletContextName();
        if(Strings.isNotEmpty(context)){
            context = request.getContextPath().substring(1);
        }
        service.setServiceName(context);
        return service;
    }

    @Override
    public Principal extractPrincipal(AuditRequest<HttpServletRequest, Method> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        Principal principal = new Principal();
        principal.setClientHost(request.getRemoteHost());
        principal.setClientIp(request.getRemoteAddr());
        principal.setClientPort(request.getRemotePort());
        principal.setPrincipalId(request.getRemoteUser());
        principal.setPrincipalName(request.getRemoteUser());
        principal.setPrincipalType(Strings.isEmpty(request.getAuthType()) ? PrincipalType.anonymous : PrincipalType.authenticated);
        return principal;
    }


}
