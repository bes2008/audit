package com.jn.audit.mq.producer;

import com.jn.audit.mq.MessageTopicDispatcher;
import com.jn.audit.mq.Producer;
import com.jn.langx.util.Objects;

public class FixedTopicProducer<M> implements Producer<M> {

    private final String topic;

    public FixedTopicProducer(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public void publish(String topicName, M message) {
        if (Objects.isNotNull(message)) {
            MessageTopicDispatcher dispatcher = MessageTopicDispatcher.getInstance();
            dispatcher.publish(getTopic(), message);
        }
    }

    @Override
    public String apply(M input) {
        return topic;
    }
}
