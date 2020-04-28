package com.jn.audit.spring.simple;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.Auditor;
import com.jn.audit.core.model.AuditEvent;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate2;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.bind.WebDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * AOP expression:
 *  execution(public * your.controller.package..*Controller.*(..))
 */
public class ControllerMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        AuditRequest request = Auditor.auditRequestHolder.get();
        if (request != null) {
            AuditEvent event = request.getAuditEvent();
            if (event != null) {
                Map<String, Object> map = Collects.emptyHashMap();
                Method method = invocation.getMethod();
                Parameter[] parameters = method.getParameters();
                Object[] args = invocation.getArguments();
                Collects.forEach(args, new Predicate2<Integer, Object>() {
                    @Override
                    public boolean test(Integer index, Object arg) {
                        if (arg == null) {
                            return false;
                        }
                        if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse || arg instanceof WebDataBinder) {
                            return false;
                        }

                        String packageName = parameters[index].getType().getPackage().getName();
                        if (packageName.startsWith("java.lang.reflect.") || packageName.startsWith("org.springframework.")) {
                            return false;
                        }

                        return true;
                    }
                }, new Consumer2<Integer, Object>() {
                    @Override
                    public void accept(Integer index, Object arg) {
                        String parameterName = parameters[index].getName();
                        map.put(parameterName, arg);
                    }
                }, Functions.falsePredicate2());

                event.getOperation().setParameters(map);
            }
        }
        return invocation.proceed();
    }
}
