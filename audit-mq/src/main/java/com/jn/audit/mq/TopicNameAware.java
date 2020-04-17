package com.jn.audit.mq;

public interface TopicNameAware {
    void setTopicName(String topic);
    String getTopicName();
}
