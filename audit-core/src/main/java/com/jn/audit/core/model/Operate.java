package com.jn.audit.core.model;

import java.io.Serializable;

public class Operate implements Serializable {
    public static final long serialVersionUID = 1L;

    private String operateCode;
    private String operateName;
    private String parameters;

    public String getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(String operateCode) {
        this.operateCode = operateCode;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
