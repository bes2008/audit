package com.jn.audit.mq.allocator;

import com.jn.audit.mq.MessageTopic;
import com.jn.audit.mq.TopicAllocator;
import com.jn.audit.mq.event.TopicEvent;
import com.jn.audit.mq.event.TopicEventType;
import com.jn.langx.util.collection.Collects;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractMultipleCandidateTopicAllocator<M> implements TopicAllocator<M> {

    private final Set<String> watchedTopics = Collects.newHashSet();
    protected final Set<String> validTopics = new CopyOnWriteArraySet<String>();

    public void setCandidateTopics(List<String> candidateTopics) {
        this.watchedTopics.addAll(candidateTopics);
    }

    @Override
    public void on(TopicEvent event) {
        if (event.getType() == TopicEventType.REMOVE) {
            MessageTopic topic = event.getSource();
            this.validTopics.remove(topic.getName());
            return;
        }

        if (event.getType() == TopicEventType.ADD) {
            MessageTopic topic = event.getSource();
            if (this.watchedTopics.contains(topic.getName())) {
                this.validTopics.add(topic.getName());
            }
            return;
        }

    }
}
