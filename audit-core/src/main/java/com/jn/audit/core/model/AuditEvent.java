package com.jn.audit.core.model;

import java.io.Serializable;

public class AuditEvent implements Serializable {
    public static final long serialVersionUID = 1L;

    // who
    private Principal principal; // {required}

    // when
    private long time;  // {required} UTC time

    /**
     * service , also the main target
     */
    private Service service; // {required}

    /**
     * target, also the subject target
     */
    private Resource resource; // {optional}

    /**
     * do what
     */
    private Operate operate; // {required}

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public Operate getOperate() {
        return operate;
    }

    public void setOperate(Operate operate) {
        this.operate = operate;
    }
}
