package com.jn.audit.spring.webmvc;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.operation.method.AbstractOperationMethodIdGenerator;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class RequestMappingOperationDefinitionIdGenerator extends AbstractOperationMethodIdGenerator<HttpServletRequest> {
    @Override
    public String get(AuditRequest<HttpServletRequest, MethodInvocation> auditRequest) {
        MethodInvocation methodInvocation = auditRequest.getRequestContext();
        Method method = methodInvocation.getJoinPoint();
        if (RequestMappings.hasAnyRequestMappingAnnotation(method)) {
            Annotation mappingOfMethod = RequestMappings.findFirstRequestMappingAnnotation(method);
            RequestMappingAccessor<?> accessor = RequestMappingAccessorFactory.createAccessor(mappingOfMethod);
            List<RequestMethod> httpMethods = accessor.getMethods();
            List<String> paths = accessor.getPaths();
            if (Emptys.isEmpty(paths)) {
                paths = accessor.getValues();
            }
            List<String> controllerPaths = RequestMappings.getURLTemplates(method.getDeclaringClass());
            if (Objs.isEmpty(httpMethods)) {
                return null;
            }

            String urlTemplate = "";
            if (Emptys.isEmpty(paths) && Emptys.isEmpty(controllerPaths)) {
                urlTemplate = "/";
            } else {
                String urlAtController = Emptys.isEmpty(controllerPaths) ? "" : controllerPaths.get(0);
                String urlAtMethod = Emptys.isEmpty(paths) ? "" : paths.get(0);
                if (Objs.isEmpty(urlAtController)) {
                    urlTemplate = urlAtMethod;
                } else {
                    if(Strings.isNotEmpty(urlAtMethod)) {
                        if (urlAtMethod.startsWith("/")) {
                            urlTemplate = urlAtController + urlAtMethod;
                        } else {
                            urlTemplate = urlAtController + "/" + urlAtMethod;
                        }
                    }
                }
            }
            return httpMethods.get(0).name() + "-" + urlTemplate;
        }
        return null;
    }
}
