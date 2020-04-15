package com.jn.audit.core.model;

import java.io.Serializable;

public class AuditEvent implements Serializable {
    public static final long serialVersionUID = 1L;

    // who
    private Principal principal; // {required}

    // when
    private long startTime;// {required} UTC time
    private long endTime;// {required} UTC time
    private long duration;  // endTime - startTime

    /**
     * service , also the main target
     */
    private Service service; // {required}

    /**
     * target, also the subject target
     * maybe extract from Operation parameters
     */
    private Resource resource; // {optional}

    /**
     * do what
     */
    private Operation operation; // {required}


    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }


    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
