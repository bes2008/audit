package com.jn.audit.core.model;

import java.io.Serializable;

/**
 * may be in any style: xml, yaml, database
 */
public class OperationDefine implements Serializable {
    public static final long serialVersionUID = 1L;

    private String code;// {required}   // id
    private String name;// {required}
    private String type;// {optional}
    private String description;// {optional}
    private OperationImportance importance;  // {optional}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationImportance getImportance() {
        return importance;
    }

    public void setImportance(OperationImportance importance) {
        this.importance = importance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
