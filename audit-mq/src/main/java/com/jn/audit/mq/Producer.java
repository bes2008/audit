package com.jn.audit.mq;

public interface Producer<M> {
    void publish(String topicName, M message);
}
