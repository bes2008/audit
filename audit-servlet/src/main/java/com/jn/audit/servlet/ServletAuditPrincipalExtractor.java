package com.jn.audit.servlet;

import com.jn.audit.core.PrincipalExtractor;
import com.jn.audit.core.model.Principal;

import javax.servlet.http.HttpServletRequest;

public class ServletAuditPrincipalExtractor implements PrincipalExtractor<HttpServletRequest> {
    @Override
    public Principal get(HttpServletRequest request) {
        Principal principal = new Principal();
        AuditServlets.setRemote(request, principal);
        setPrincipalBySession(request, principal);
        return principal;
    }

    protected void setPrincipalBySession(HttpServletRequest request, Principal principal){
        // NOOP
    }


}
