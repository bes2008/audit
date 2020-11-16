package com.jn.audit.entityloader.resttemplate;

import com.jn.audit.core.model.ResourceDefinition;
import org.springframework.http.HttpMethod;

public interface ParameterizedResponseClassProvider {
    Class get(String url, HttpMethod httpMethod, ResourceDefinition resourceDefinition);
}
