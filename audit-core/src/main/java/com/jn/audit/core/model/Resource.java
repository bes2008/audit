package com.jn.audit.core.model;

import java.io.Serializable;

public class Resource implements Serializable {
    public static final long serialVersionUID = 1L;

    private String resourceId;
    private String resourceType;
    private String resourceName;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
