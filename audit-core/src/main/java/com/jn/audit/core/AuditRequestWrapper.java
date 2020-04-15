package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;

public class AuditRequestWrapper<AuditedRequest> {
    private AuditEvent auditEvent;
    private AuditedRequest request;

    public AuditEvent getAuditEvent() {
        return auditEvent;
    }

    public void setAuditEvent(AuditEvent auditEvent) {
        this.auditEvent = auditEvent;
    }

    public AuditedRequest getRequest() {
        return request;
    }

    public void setRequest(AuditedRequest request) {
        this.request = request;
    }
}
