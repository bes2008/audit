package com.jn.audit.entityloader.resttemplate;

import org.springframework.http.ResponseEntity;

public interface ResourceEntityExtractor {
    Object extract(ResponseEntity response);
}
