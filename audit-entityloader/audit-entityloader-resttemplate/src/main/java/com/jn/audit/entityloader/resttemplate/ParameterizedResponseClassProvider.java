package com.jn.audit.entityloader.resttemplate;

import com.jn.audit.core.model.ResourceDefinition;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

public interface ParameterizedResponseClassProvider {
    <T> ParameterizedTypeReference<T> get(String url, HttpMethod httpMethod, ResourceDefinition resourceDefinition);
}
