package com.jn.audit.servlet;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.operation.method.AbstractOperationMethodIdGenerator;
import com.jn.langx.util.Strings;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class ServletUrlOperationIdGenerator extends AbstractOperationMethodIdGenerator<HttpServletRequest> {
    @Override
    public String get(AuditRequest<HttpServletRequest, Method> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        String context = request.getContextPath();
        String uri = request.getRequestURI().substring(context.length());
        if (Strings.isEmpty(uri)) {
            uri = "/";
        }
        StringBuilder builder = new StringBuilder(255)
                .append(request.getMethod())
                .append("-")
                .append(uri);
        return builder.toString();
    }

}
