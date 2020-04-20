package com.jn.audit.servlet;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.operation.OperationParametersExtractor;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.Charsets;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServletHttpParametersExtractor implements OperationParametersExtractor<HttpServletRequest, Method> {
    private String encoding = Charsets.UTF_8.name();

    @Override
    public Map<String, List<? extends Serializable>> get(AuditRequest<HttpServletRequest, Method> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        try {
            request.setCharacterEncoding(encoding);
        } catch (UnsupportedEncodingException ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        final Map<String, List<? extends Serializable>> ret = new LinkedHashMap<String, List<? extends Serializable>>();
        Collects.forEach(parameterMap, new Consumer2<String, String[]>() {
            @Override
            public void accept(String key, String[] values) {
                ret.put(key, Collects.newArrayList(values));
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
