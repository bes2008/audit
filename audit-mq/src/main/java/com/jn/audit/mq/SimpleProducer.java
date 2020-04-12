package com.jn.audit.mq;

import com.jn.langx.util.Objects;

public class SimpleProducer<M> implements Producer<M> {
    @Override
    public void publish(String topicName, M message) {
        if (Objects.isNotNull(message)) {
            MessageTopicDispatcher dispatcher = MessageTopicDispatcher.getInstance();
            dispatcher.publish(topicName, message);
        }
    }
}
