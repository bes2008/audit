package com.jn.audit.entityloader.resttemplate;

import com.jn.audit.core.model.ResourceDefinition;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public interface RestTemplateProvider {
    RestTemplate get(String url, HttpMethod httpMethod, ResourceDefinition resourceDefinition);
}
