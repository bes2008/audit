package com.jn.audit.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Operation implements Serializable {
    public static final long serialVersionUID = 1L;

    private String operateCode;
    private String operateName;
    private Map<String, List<String>> parameters;

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

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }
}
