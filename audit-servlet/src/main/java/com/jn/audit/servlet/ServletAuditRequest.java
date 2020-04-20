package com.jn.audit.servlet;

import com.jn.audit.core.AuditRequest;
import com.jn.langx.text.StringTemplates;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletAuditRequest extends AuditRequest<HttpServletRequest, Method> {

    public ServletAuditRequest(HttpServletRequest request, Method method) {
        setRequest(request);
        setRequestContext(method);
    }

    @Override
    public String toString() {
        return StringTemplates.formatWithPlaceholder("servletPath:{}, url:{}", getRequest().getServletPath(), getRequest().getRequestURL().toString());
    }
}
