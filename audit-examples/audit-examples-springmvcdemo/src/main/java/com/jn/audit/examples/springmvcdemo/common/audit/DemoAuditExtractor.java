package com.jn.audit.examples.springmvcdemo.common.audit;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.*;
import com.jn.audit.servlet.ServletAuditEventExtractor;
import com.jn.langx.util.Strings;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
public class DemoAuditExtractor extends ServletAuditEventExtractor {
    @Override
    public void extractService(AuditRequest<HttpServletRequest, Method> wrappedRequest, AuditEvent event, Service service) {
        HttpServletRequest request = wrappedRequest.getRequest();
        service.setServicePort(request.getLocalPort());
        service.setServiceIp(request.getLocalAddr());
        service.setServiceProtocol(request.getProtocol());
        service.setServiceName(request.getServletContext().getServletContextName());
    }

    @Override
    public void extractPrincipal(AuditRequest<HttpServletRequest, Method> wrappedRequest, AuditEvent event, Principal principal) {
        HttpServletRequest request = wrappedRequest.getRequest();
        principal.setClientHost(request.getRemoteHost());
        principal.setClientIp(request.getRemoteAddr());
        principal.setClientPort(request.getRemotePort());
        principal.setPrincipalId(request.getRemoteUser());
        principal.setPrincipalName(request.getRemoteUser());
        principal.setPrincipalType(Strings.isEmpty(request.getAuthType()) ? PrincipalType.anonymous : PrincipalType.authenticated);
    }

    @Override
    public void extractResource(AuditRequest<HttpServletRequest, Method> wrappedRequest, AuditEvent event, Resource resource) {

    }

    @Override
    public void extractOperation(AuditRequest<HttpServletRequest, Method> wrappedRequest, AuditEvent event, Operation operation) {

    }
}
