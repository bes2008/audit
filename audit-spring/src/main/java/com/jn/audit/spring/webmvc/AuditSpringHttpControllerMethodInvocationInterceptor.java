package com.jn.audit.spring.webmvc;

import com.jn.agileway.aop.adapter.aopalliance.MethodInvocationAdapter;
import com.jn.agileway.web.filter.rr.RRHolder;
import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.Auditor;
import com.jn.audit.core.model.AuditEvent;
import com.jn.audit.core.model.Resource;
import com.jn.audit.core.operation.method.OperationParameterMethodInvocationExtractor;
import com.jn.audit.core.resource.ResourceMethodInvocationExtractor;
import com.jn.langx.util.function.Predicate;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class AuditSpringHttpControllerMethodInvocationInterceptor implements MethodInterceptor, InitializingBean {
    private OperationParameterMethodInvocationExtractor operationParameterExtractor;
    private ResourceMethodInvocationExtractor resourceExtractor;
    private Auditor httpAuditor;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        AuditRequest request = Auditor.auditRequestHolder.get();
        if (request != null) {
            AuditEvent event = request.getAuditEvent();
            if (event != null && request.isAuditIt()) {
                com.jn.langx.invocation.MethodInvocation commonMethodInvocation = new MethodInvocationAdapter(invocation);
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
        } else {
            HttpServletRequest servletRequest = RRHolder.getRequest();
            com.jn.langx.invocation.MethodInvocation commonMethodInvocation = new MethodInvocationAdapter(invocation);
            httpAuditor.startAudit(servletRequest, commonMethodInvocation);
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

    @Autowired
    @Qualifier("springHttpAuditor")
    public void setHttpAuditor(Auditor httpAuditor) {
        this.httpAuditor = httpAuditor;
    }
}