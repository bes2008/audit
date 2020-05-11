package com.jn.audit.spring.webmvc;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.Auditor;
import com.jn.audit.core.model.OperationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuditHttpHandlerInterceptor implements HandlerInterceptor {
    /**
     * Spring Boot WebMvc 环境下，会将 error 放到 request.attributes中
     */
    private static final String SPRING_BOOT_WEBMVC_ERROR_ATTRIBUTE = "org.springframework.boot.web.servlet.error.DefaultErrorAttributes.ERROR";

    /**
     * Spring Boot WebFlux 环境下，会将 error 放到 exchange 下
     */
    private static final String SPRING_BOOT_WEBFLUX_ERROR_ATTRIBUTE = "org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR";

    @Autowired
    private Auditor<HttpServletRequest, Method> auditor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            auditor.startAudit(request, method);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            AuditRequest<HttpServletRequest, Method> wrappedRequest = Auditor.auditRequestHolder.get();
            if (wrappedRequest != null) {
                OperationResult result = null;
                if (ex != null) {
                    result = OperationResult.FAIL;
                } else {
                    result = request.getAttribute(SPRING_BOOT_WEBMVC_ERROR_ATTRIBUTE) == null ? OperationResult.SUCCESS : OperationResult.FAIL;
                }
                wrappedRequest.setResult(result);
                auditor.finishAudit(wrappedRequest);
            }
        }
    }
}
