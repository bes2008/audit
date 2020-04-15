package com.jn.audit.core;

import com.jn.audit.mq.MessageTopicConfiguration;

import java.io.Serializable;

public class AuditSettings implements Serializable {
    public static final long serialVersionUID = 1L;

    /**************************************************
     * Auditor
     **************************************************/
    /**
     * @ses {@link Auditor#setAsyncAudit(boolean)}
     */
    private boolean asyncMode = false;


    /**************************************************
     * memory message queue
     **************************************************/
    private MessageTopicConfiguration topic;
    private String consumerWaitStrategy = "blocking";



    /**************************************************
     *  Operation Repository Settings
     **************************************************/
    /**
     * units: seconds
     * scan interval, if <=0, will not refresh
     *
     * @see {@link com.jn.audit.core.operation.OperationDefinitionRepository#setReloadIntervalInSeconds(int)}
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

    public boolean isAsyncMode() {
        return asyncMode;
    }

    public void setAsyncMode(boolean asyncMode) {
        this.asyncMode = asyncMode;
    }

    public MessageTopicConfiguration getTopic() {
        return topic;
    }

    public void setTopic(MessageTopicConfiguration topic) {
        this.topic = topic;
    }

    public String getConsumerWaitStrategy() {
        return consumerWaitStrategy;
    }

    public void setConsumerWaitStrategy(String consumerWaitStrategy) {
        this.consumerWaitStrategy = consumerWaitStrategy;
    }
}
