package com.jn.audit.servlet;

import com.jn.audit.core.AbstractAuditEventExtractor;
import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletAuditEventExtractor extends AbstractAuditEventExtractor<HttpServletRequest, Method> {
    @Override
    public void extractService(AuditRequest<HttpServletRequest, Method> wrappedRequest, AuditEvent event, Service service) {

    }

    @Override
    public void extractPrincipal(AuditRequest<HttpServletRequest, Method> wrappedRequest, AuditEvent event, Principal principal) {

    }

    @Override
    public void extractResource(AuditRequest<HttpServletRequest, Method> wrappedRequest, AuditEvent event, Resource resource) {

    }

    @Override
    public void extractOperation(AuditRequest<HttpServletRequest, Method> wrappedRequest, AuditEvent event, Operation operation) {

    }
}
