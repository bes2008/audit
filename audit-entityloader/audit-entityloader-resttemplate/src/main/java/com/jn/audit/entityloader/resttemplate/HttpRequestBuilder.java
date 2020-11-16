package com.jn.audit.entityloader.resttemplate;

import com.jn.langx.Builder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

public interface HttpRequestBuilder extends Builder<HttpEntity> {
    HttpRequestBuilder url(String url);
    HttpRequestBuilder method(HttpMethod method);
    @Override
    HttpEntity build();
}
