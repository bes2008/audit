package com.jn.audit.core.operation;


import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.AuditEvent;
import com.jn.langx.proxy.aop.MethodInvocation;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.reflect.Parameter;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.parameter.MethodParameter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class OperationParameterMethodInvocationExtractor<AuditedRequest> implements OperationParametersExtractor<AuditedRequest, MethodInvocation> {
    private List<Predicate<Object>> parameterValueExclusionPredicates = Collects.<Predicate<Object>>newArrayList();

    public void addExclusionPredicate(Predicate<Object> predicate) {
        parameterValueExclusionPredicates.add(predicate);
    }

    @Override
    public Map<String, Object> get(AuditRequest<AuditedRequest, MethodInvocation> wrappedRequest) {
        if (wrappedRequest == null) {
            return null;
        }
        AuditEvent event = wrappedRequest.getAuditEvent();
        if (event == null) {
            return null;
        }
        final Map<String, Object> map = Collects.emptyHashMap();
        MethodInvocation invocation = wrappedRequest.getRequestContext();
        Method method = invocation.getJoinPoint();
        final List<MethodParameter> parameters = Reflects.getMethodParameters(method);
        Object[] args = invocation.getArguments();
        Collects.forEach(args, new Predicate2<Integer, Object>() {
            @Override
            public boolean test(Integer index, final Object arg) {
                if (arg == null) {
                    return false;
                }
                if (Collects.anyMatch(parameterValueExclusionPredicates, new Predicate<Predicate<Object>>() {
                    @Override
                    public boolean test(Predicate<Object> predicate) {
                        return predicate.test(arg);
                    }
                })) {
                    return false;
                }

                Parameter parameter = parameters.get(index);
                if (parameter == null || parameter.getType() == null || parameter.getType().getPackage() == null) {
                    return false;
                }
                String packageName = parameter.getType().getPackage().getName();
                if (packageName.startsWith("java.lang.reflect.") || packageName.startsWith("org.springframework.")) {
                    return false;
                }

                return true;
            }
        }, new Consumer2<Integer, Object>() {
            @Override
            public void accept(Integer index, Object arg) {
                String parameterName = parameters.get(index).getName();
                map.put(parameterName, arg);
            }
        }, Functions.<Integer, Object>falsePredicate2());

        return map;
    }
}
