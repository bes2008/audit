package com.jn.audit.spring.webmvc;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.operation.method.AbstractOperationMethodIdGenerator;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.reflect.Reflects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class GetMappingOperationDefinitionIdGenerator extends AbstractOperationMethodIdGenerator<HttpServletRequest> {
    @Override
    public String get(AuditRequest<HttpServletRequest, Method> auditRequest) {
        Method method = auditRequest.getRequestContext();
        GetMapping mappingOfMethod = Reflects.getAnnotation(method, GetMapping.class);
        if (mappingOfMethod!=null) {
            String httpMethod = RequestMethod.GET.name();
            String[] paths = RequestMappings.getURLTemplates(mappingOfMethod);
            String[] controllerPaths = RequestMappings.getURLTemplates(method.getDeclaringClass());
            if (Strings.isEmpty(httpMethod) || Emptys.isEmpty(paths) || Emptys.isEmpty(controllerPaths)) {
                return null;
            }
            return httpMethod + "-" + controllerPaths[0] + paths[0];
        }
        return null;
    }
}
