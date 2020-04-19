package com.jn.audit.spring.webmvc;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.operation.method.AbstractOperationMethodIdGenerator;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objects;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class RequestMappingOperationDefinitionIdGenerator extends AbstractOperationMethodIdGenerator<HttpServletRequest> {
    @Override
    public String get(AuditRequest<HttpServletRequest, Method> auditRequest) {
        Method method = auditRequest.getRequestContext();
        if (RequestMappings.hasAnyRequestMappingAnnotation(method)) {
            Annotation mappingOfMethod = RequestMappings.findFirstRequestMappingAnnotation(method);
            RequestMappingAccessor<?> accessor = RequestMappingAccessorFactory.createAccessor(mappingOfMethod);
            List<RequestMethod> httpMethods = accessor.getMethods();
            List<String> paths = accessor.getPaths();
            List<String> controllerPaths = RequestMappings.getURLTemplates(method.getDeclaringClass());
            if (Objects.isEmpty(httpMethods) || Emptys.isEmpty(paths) || Emptys.isEmpty(controllerPaths)) {
                return null;
            }
            return httpMethods.get(0).name() + "-" + controllerPaths.get(0) + paths.get(0);
        }
        return null;
    }
}
