package com.jn.audit.core;

import com.jn.audit.mq.DefaultMessageTranslator;
import com.jn.audit.mq.MessageTopicConfiguration;
import com.jn.audit.mq.allocator.DefaultTopicAllocator;
import com.jn.langx.util.reflect.Reflects;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executor;

public class AuditSettings implements Serializable {
    public static final long serialVersionUID = 1L;

    /**************************************************
     * Auditor
     **************************************************/
    /**
     * @ses {@link Auditor#setAsyncAudit(boolean)}
     */
    private boolean asyncMode = true;

    private Executor executor;


    /**************************************************
     * memory message queue
     **************************************************/
    private List<MessageTopicConfiguration> topics;
    /**
     * specified default consumer wait strategy.
     * <p>
     * if a wait strategy in {@link #topics} is not specified, will using it
     *
     * @see com.jn.audit.mq.BuiltinWaitStrategyFactory
     */
    private String consumerWaitStrategy = "blocking";
    /**
     * the class name of your custom message translator.
     */
    private String messageTranslator = Reflects.getFQNClassName(DefaultMessageTranslator.class);

    private String topicAllocator = Reflects.getFQNClassName(DefaultTopicAllocator.class);


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

    public List<MessageTopicConfiguration> getTopics() {
        return topics;
    }

    public void setTopics(List<MessageTopicConfiguration> topics) {
        this.topics = topics;
    }

    public String getConsumerWaitStrategy() {
        return consumerWaitStrategy;
    }

    public void setConsumerWaitStrategy(String consumerWaitStrategy) {
        this.consumerWaitStrategy = consumerWaitStrategy;
    }

    public String getMessageTranslator() {
        return messageTranslator;
    }

    public void setMessageTranslator(String messageTranslator) {
        this.messageTranslator = messageTranslator;
    }

    public String getTopicAllocator() {
        return topicAllocator;
    }

    public void setTopicAllocator(String topicAllocator) {
        this.topicAllocator = topicAllocator;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
