package com.jn.audit.servlet;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.PrincipalExtractor;
import com.jn.audit.core.model.Principal;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletAuditPrincipalExtractor implements PrincipalExtractor<HttpServletRequest, Method> {
    @Override
    public Principal get(AuditRequest<HttpServletRequest,Method> wrappedRequest) {
        Principal principal = new Principal();
        AuditServlets.setRemote(wrappedRequest.getRequest(), principal);
        setPrincipalBySession(wrappedRequest.getRequest(), principal);
        return principal;
    }

    protected void setPrincipalBySession(HttpServletRequest request, Principal principal){
        // NOOP
    }


}
