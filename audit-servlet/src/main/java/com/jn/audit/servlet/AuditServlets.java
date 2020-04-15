package com.jn.audit.servlet;

import com.jn.audit.core.model.Principal;

import javax.servlet.http.HttpServletRequest;

public class AuditServlets {
    public static void setRemote(HttpServletRequest request, Principal principal) {
        principal.setClientHost(request.getRemoteHost());
        principal.setClientIp(request.getRemoteAddr());
        principal.setClientPort(request.getRemotePort());
    }
}
