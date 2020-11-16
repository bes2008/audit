package com.jn.audit.entityloader.resttemplate;

import org.springframework.http.ResponseEntity;

public class DefaultResourceEntityExtractor implements ResourceEntityExtractor{
    @Override
    public Object extract(ResponseEntity response) {
        return response.getBody();
    }
}
