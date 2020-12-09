package com.jn.audit.servlet;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Service;
import com.jn.audit.core.service.ServiceExtractor;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Strings;

import javax.servlet.http.HttpServletRequest;

public class ServletAuditEventServiceExtractor implements ServiceExtractor<HttpServletRequest, MethodInvocation> {
    @Override
    public Service get(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
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
}
