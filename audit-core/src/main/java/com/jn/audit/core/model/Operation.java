package com.jn.audit.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Operation implements Serializable {
    private OperationDefinition define;
    private Map<String, List<String>> parameters; // {optional}
    private OperationResult result;

    public OperationDefinition getDefine() {
        return define;
    }

    public void setDefine(OperationDefinition define) {
        this.define = define;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public OperationResult getResult() {
        return result;
    }

    public void setResult(OperationResult result) {
        this.result = result;
    }
}
