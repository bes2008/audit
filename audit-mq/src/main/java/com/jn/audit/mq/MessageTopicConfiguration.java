package com.jn.audit.mq;

import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;

public class MessageTopicConfiguration {
    private int ringBufferSize; // power(2)
    private Executor executor;
    private ProducerType producerType;
    private WaitStrategy waitStrategy;
    private MessageTranslator messageTranslator;

    public int getRingBufferSize() {
        return ringBufferSize;
    }

    public void setRingBufferSize(int ringBufferSize) {
        this.ringBufferSize = ringBufferSize;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public ProducerType getProducerType() {
        return producerType;
    }

    public void setProducerType(ProducerType producerType) {
        this.producerType = producerType;
    }

    public WaitStrategy getWaitStrategy() {
        return waitStrategy;
    }

    public void setWaitStrategy(WaitStrategy waitStrategy) {
        this.waitStrategy = waitStrategy;
    }

    public MessageTranslator getMessageTranslator() {
        return messageTranslator;
    }

    public void setMessageTranslator(MessageTranslator messageTranslator) {
        this.messageTranslator = messageTranslator;
    }
}
