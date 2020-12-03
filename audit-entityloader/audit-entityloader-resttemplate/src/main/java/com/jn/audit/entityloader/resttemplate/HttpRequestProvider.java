package com.jn.audit.entityloader.resttemplate;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.langx.Builder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.io.Serializable;

public interface HttpRequestProvider {
    HttpEntity get(String url, HttpMethod method, ResourceDefinition resourceDefinition, Serializable entityId);
}
