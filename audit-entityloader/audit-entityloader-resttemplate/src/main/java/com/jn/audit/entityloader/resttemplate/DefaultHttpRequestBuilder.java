package com.jn.audit.entityloader.resttemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

public class DefaultHttpRequestBuilder implements HttpRequestBuilder {
    @Override
    public HttpRequestBuilder url(String url) {
        return null;
    }

    @Override
    public HttpRequestBuilder method(HttpMethod method) {
        return null;
    }

    @Override
    public HttpEntity build() {
        return HttpEntity.EMPTY;
    }
}
