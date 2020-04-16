package com.jn.audit.mq.producer;

public class DefaultTopicAllocator<M> extends FixedTopicAllocator<M> {
    public static final String TOPIC_DEFAULT = "DEFAULT";

    public DefaultTopicAllocator() {
        super(TOPIC_DEFAULT);
    }
}
