package com.jn.audit.core.config;

public class AuditSettings {
    /**
     * units: seconds
     * scan interval, if <=0, will not refresh
     */
    private int reloadIntervalInSeconds = -1;
    private String operationDefinitionResource;

    public String getOperationDefinitionResource() {
        return operationDefinitionResource;
    }

    public void setOperationDefinitionResource(String operationDefinitionResource) {
        this.operationDefinitionResource = operationDefinitionResource;
    }

    public int getReloadIntervalInSeconds() {
        return reloadIntervalInSeconds;
    }

    public void setReloadIntervalInSeconds(int reloadIntervalInSeconds) {
        this.reloadIntervalInSeconds = reloadIntervalInSeconds;
    }
}
