package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;

public class AuditRequest<AuditedRequest> {
    private AuditEvent auditEvent;
    private AuditedRequest request;
    private boolean auditIt;

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

    public boolean isAuditIt() {
        return auditIt;
    }

    public void setAuditIt(boolean auditIt) {
        this.auditIt = auditIt;
    }
}
