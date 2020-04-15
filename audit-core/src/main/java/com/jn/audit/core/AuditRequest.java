package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;

public class AuditRequest<AuditedRequest, AuditedRequestContext> {
    private AuditEvent auditEvent;
    private AuditedRequest request;
    private AuditedRequestContext requestContext;
    private boolean auditIt = true;
    private long startTime;
    private long endTime;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

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

    public AuditedRequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(AuditedRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public String toString() {
        return request != null ? request.toString() : (requestContext != null ? requestContext.toString() : "audit it");
    }
}
