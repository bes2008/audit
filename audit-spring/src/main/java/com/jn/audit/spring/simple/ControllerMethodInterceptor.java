package com.jn.audit.spring.simple;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.Auditor;
import com.jn.audit.core.model.AuditEvent;
import com.jn.audit.core.model.Resource;
import com.jn.audit.core.operation.OperationParameterMethodInvocationExtractor;
import com.jn.audit.core.resource.ResourceMethodInvocationExtractor;
import com.jn.langx.proxy.aop.DefaultMethodInvocation;
import com.jn.langx.util.function.Predicate;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * AOP expression:
 *      execution(public * your.controller.package..*Controller.*(..))
 * </pre>
 */
public class ControllerMethodInterceptor implements MethodInterceptor, InitializingBean {
    private OperationParameterMethodInvocationExtractor operationParameterExtractor;
    private ResourceMethodInvocationExtractor resourceExtractor;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        AuditRequest request = Auditor.auditRequestHolder.get();
        if (request != null) {
            AuditEvent event = request.getAuditEvent();
            if (event != null) {
                DefaultMethodInvocation commonMethodInvocation = new DefaultMethodInvocation(invocation.getThis(), invocation.getThis(), invocation.getMethod(), invocation.getArguments());
                AuditRequest newRequest = new AuditRequest();
                newRequest.setRequestContext(commonMethodInvocation);
                newRequest.setRequest(request.getRequest());
                newRequest.setAuditEvent(request.getAuditEvent());
                newRequest.setAuditIt(request.isAuditIt());
                newRequest.setEndTime(request.getEndTime());
                newRequest.setStartTime(request.getStartTime());
                newRequest.setResult(request.getResult());
                newRequest.setTopic(request.getTopic());

                Map<String, Object> parameters = operationParameterExtractor.get(newRequest);
                event.getOperation().setParameters(parameters);

                List<Resource> resources = resourceExtractor.get(newRequest);
                event.setResources(resources);
            }
        }
        return invocation.proceed();
    }

    public void setOperationParameterExtractor(OperationParameterMethodInvocationExtractor operationParameterExtractor) {
        this.operationParameterExtractor = operationParameterExtractor;
    }

    public void setResourceExtractor(ResourceMethodInvocationExtractor resourceExtractor) {
        this.resourceExtractor = resourceExtractor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (operationParameterExtractor == null) {
            operationParameterExtractor = new OperationParameterMethodInvocationExtractor();
        }

        operationParameterExtractor.addExclusionPredicate(new Predicate<Object>() {
            @Override
            public boolean test(Object value) {
                return value instanceof HttpServletRequest || value instanceof HttpServletResponse || value instanceof WebDataBinder || value instanceof View || value instanceof ModelAndView;
            }
        });

        if (resourceExtractor == null) {
            resourceExtractor = new ResourceMethodInvocationExtractor();
        }
    }

}
