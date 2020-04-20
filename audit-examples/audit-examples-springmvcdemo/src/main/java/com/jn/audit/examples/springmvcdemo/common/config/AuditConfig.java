package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.mq.MessageTopicDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    private MessageTopicDispatcher dispatcher;




    public MessageTopicDispatcher getDispatcher() {
        return dispatcher;
    }

    @Autowired
    public void setDispatcher(MessageTopicDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
