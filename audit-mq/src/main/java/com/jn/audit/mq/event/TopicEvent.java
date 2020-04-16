package com.jn.audit.mq.event;

import com.jn.audit.mq.MessageTopic;
import com.jn.langx.event.DomainEvent;

public class TopicEvent extends DomainEvent<MessageTopic> {
    private TopicEventType type;

    public TopicEvent(String eventDomain, MessageTopic source) {
        super(eventDomain, source);
    }

    public TopicEvent(String eventDomain, MessageTopic source, TopicEventType type) {
        super(eventDomain, source);
        setType(type);
    }


    public TopicEventType getType() {
        return type;
    }

    public void setType(TopicEventType type) {
        this.type = type;
    }
}
