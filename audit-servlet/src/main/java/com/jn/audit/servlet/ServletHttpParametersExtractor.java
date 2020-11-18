package com.jn.audit.servlet;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.operation.OperationParametersExtractor;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.Charsets;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServletHttpParametersExtractor implements OperationParametersExtractor<HttpServletRequest, MethodInvocation> {
    private String encoding = Charsets.UTF_8.name();

    @Override
    public Map<String, Object> get(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        try {
            request.setCharacterEncoding(encoding);
        } catch (UnsupportedEncodingException ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        final Map<String, Object> ret = new LinkedHashMap<String, Object>();
        Collects.forEach(parameterMap, new Consumer2<String, String[]>() {
            @Override
            public void accept(String key, String[] values) {
                if (Objs.length(values) > 1) {
                    ret.put(key, Collects.newArrayList(values));
                } else {
                    ret.put(key, values[0]);
                }
            }
        });
        return ret;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
