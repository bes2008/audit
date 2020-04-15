package com.jn.audit.servlet;

import com.jn.audit.core.AuditEventExtractor;
import com.jn.audit.core.model.AuditEvent;

import javax.servlet.http.HttpServletRequest;

public class ServletAuditEventExtractor implements AuditEventExtractor<HttpServletRequest> {
    @Override
    public AuditEvent get(HttpServletRequest request) {
        return null;
    }
}
