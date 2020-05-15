package com.jn.audit.core;

import com.jn.audit.core.operation.repository.OperationDefinitionRepository;
import com.jn.dmmq.core.DefaultMessageTranslator;
import com.jn.dmmq.core.MessageTopicConfiguration;
import com.jn.dmmq.core.allocator.DefaultTopicAllocator;
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
    private List<MessageTopicConfiguration> topicConfigs;
    /**
     * specified default consumer wait strategy.
     * <p>
     * if a wait strategy in {@link #topicConfigs} is not specified, will using it
     *
     * @see com.jn.dmmq.core.BuiltinWaitStrategyFactory
     */
    private String consumerWaitStrategy = "blocking";

    /**
     * the candidate topics
     *
     * @see {@link com.jn.dmmq.core.allocator.AbstractMultipleCandidateTopicAllocator#setCandidateTopics(List)}
     */
    private List<String> topics;

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

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
     * @see {@link OperationDefinitionRepository#setReloadIntervalInSeconds(int)}
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

    public List<MessageTopicConfiguration> getTopicConfigs() {
        return topicConfigs;
    }

    public void setTopicConfigs(List<MessageTopicConfiguration> topicConfigs) {
        this.topicConfigs = topicConfigs;
    }

    public String getConsumerWaitStrategy() {
        return consumerWaitStrategy;
    }

    public void setConsumerWaitStrategy(String consumerWaitStrategy) {
        this.consumerWaitStrategy = consumerWaitStrategy;
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
