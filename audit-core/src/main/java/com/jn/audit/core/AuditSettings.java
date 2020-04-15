package com.jn.audit.core;

import com.jn.audit.mq.MessageTopicConfiguration;

public class AuditSettings {

    /**************************************************
     *
     **************************************************/
    private MessageTopicConfiguration topic;

    /**************************************************
     *  Operation Repository Settings
     **************************************************/
    /**
     * units: seconds
     * scan interval, if <=0, will not refresh
     */
    private int operationDefinitionReloadIntervalInSeconds = -1;
    private String operationDefinitionResource;

    public String getOperationDefinitionResource() {
        return operationDefinitionResource;
    }

    public void setOperationDefinitionResource(String operationDefinitionResource) {
        this.operationDefinitionResource = operationDefinitionResource;
    }

    public int getOperationDefinitionReloadIntervalInSeconds() {
        return operationDefinitionReloadIntervalInSeconds;
    }

    public void setOperationDefinitionReloadIntervalInSeconds(int operationDefinitionReloadIntervalInSeconds) {
        this.operationDefinitionReloadIntervalInSeconds = operationDefinitionReloadIntervalInSeconds;
    }
}
