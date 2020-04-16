package com.jn.audit.mq.producer;

import com.jn.audit.mq.TopicAllocator;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;

public class FixedTopicAllocator<M> implements TopicAllocator<M> {

    private final String topic;

    public FixedTopicAllocator(@NonNull String topic) {
        Preconditions.checkNotNull(topic);
        this.topic = topic;
    }

    @Override
    public String apply(M input) {
        return topic;
    }
}
