package com.jn.audit.core.model;

import java.io.Serializable;
import java.util.HashMap;

public class Resource extends HashMap<String, Object> implements Serializable {
    public static final long serialVersionUID = 1L;
    public static final String RESOURCE_ID = "resourceId";
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String RESOURCE_NAME = "resourceName";
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
