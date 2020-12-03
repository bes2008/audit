package com.jn.audit.entityloader.resttemplate;

import com.jn.audit.core.model.ResourceDefinition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.io.Serializable;

public class DefaultHttpRequestProvider implements HttpRequestProvider {
    @Override
    public HttpEntity get(String url, HttpMethod method, ResourceDefinition resourceDefinition, Serializable entityId) {
        return HttpEntity.EMPTY;
    }
}
