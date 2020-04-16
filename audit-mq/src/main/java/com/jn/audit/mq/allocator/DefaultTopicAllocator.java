package com.jn.audit.mq.allocator;

public class DefaultTopicAllocator<M> extends FixedTopicAllocator<M> {
    public static final String TOPIC_DEFAULT = "DEFAULT";

    public DefaultTopicAllocator() {
        super(TOPIC_DEFAULT);
    }
}
