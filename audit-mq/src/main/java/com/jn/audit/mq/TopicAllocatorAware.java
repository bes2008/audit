package com.jn.audit.mq;

public interface TopicAllocatorAware<M> {

    void setTopicAllocator(TopicAllocator<M> topicAllocator);

    TopicAllocator<M> getTopicAllocator();
}
