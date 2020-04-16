package com.jn.audit.mq;

public interface MessageTopicDispatcherAware {
    MessageTopicDispatcher getMessageTopicDispatcher();
    void setMessageTopicDispatcher(MessageTopicDispatcher dispatcher);
}
