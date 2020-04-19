package com.jn.audit.spring.webmvc;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.operation.method.AbstractOperationMethodIdGenerator;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objects;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

public class RequestMappingOperationDefinitionIdGenerator extends AbstractOperationMethodIdGenerator<HttpServletRequest> {
    @Override
    public String get(AuditRequest<HttpServletRequest, Method> auditRequest) {
        Method method = auditRequest.getRequestContext();
        if (RequestMappings.hasRequestMappingAnnotation(method)) {
            RequestMapping mappingOfMethod = RequestMappings.getRequestMapping(method);
            RequestMethod httpMethod = RequestMappings.getRequestMethod(method);
            List<String> paths = RequestMappings.getURLTemplates(mappingOfMethod);
            List<String> controllerPaths = RequestMappings.getURLTemplates(method.getDeclaringClass());
            if (Objects.isNotEmpty(httpMethod) || Emptys.isEmpty(paths) || Emptys.isEmpty(controllerPaths)) {
                return null;
            }
            return httpMethod + "-" + controllerPaths.get(0) + paths.get(0);
        }
        return null;
    }
}
