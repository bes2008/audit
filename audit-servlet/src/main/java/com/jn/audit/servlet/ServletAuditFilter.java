package com.jn.audit.servlet;

import com.jn.audit.core.Auditor;
import com.jn.langx.invocation.MethodInvocation;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;

// 可以参考 com.jn.audit.spring.webmvc.AuditHttpHandlerInterceptor 的写法
public class ServletAuditFilter implements Filter {

    private Auditor<HttpServletRequest, MethodInvocation> auditor;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request instanceof HttpServletRequest) {
            // how to get the method ?
           // auditor.startAudit(request, method);
        }
    }

    @Override
    public void destroy() {

    }
}
