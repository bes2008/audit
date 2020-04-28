package com.jn.audit.core.model;

import com.jn.langx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Operation implements Serializable {
    private OperationDefinition definition;
    private Map<String, ?> parameters; // {optional}
    private OperationResult result;

    public OperationDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(OperationDefinition definition) {
        this.definition = definition;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ?> parameters) {
        this.parameters = parameters;
    }

    public OperationResult getResult() {
        return result;
    }

    public void setResult(OperationResult result) {
        this.result = result;
    }

    public static void copyTo(@NonNull Operation source, @NonNull Operation destination) {
        destination.setResult(source.getResult());
        destination.setParameters(source.getParameters());
        destination.setDefinition(source.getDefinition());
    }
}
