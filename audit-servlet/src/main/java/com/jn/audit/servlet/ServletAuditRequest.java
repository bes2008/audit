package com.jn.audit.servlet;

import com.jn.audit.core.AuditRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletAuditRequest extends AuditRequest<HttpServletRequest, Method> {

    public ServletAuditRequest(HttpServletRequest request, Method method){
        setRequest(request);
        setRequestContext(method);
    }

    @Override
    public String toString() {
        return "servletPath:{}" + getRequest().getServletPath() + " url:" + getRequest().getRequestURL().toString();
    }
}
