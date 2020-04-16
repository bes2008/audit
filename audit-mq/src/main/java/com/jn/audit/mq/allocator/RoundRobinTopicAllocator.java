package com.jn.audit.mq.allocator;

import com.jn.audit.mq.MessageTopic;
import com.jn.audit.mq.TopicAllocator;
import com.jn.audit.mq.event.TopicEvent;
import com.jn.audit.mq.event.TopicEventType;
import com.jn.langx.util.collection.Collects;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class RoundRobinTopicAllocator<M> implements TopicAllocator<M> {
    private final Set<String> watchedTopics = Collects.newHashSet();
    private final Set<String> validTopics = new CopyOnWriteArraySet<String>();

    private volatile int nextIndex = 0;

    public RoundRobinTopicAllocator(List<String> topics) {
        watchedTopics.addAll(topics);
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

    @Override
    public String apply(M input) {
        int i = nextIndex;
        if (nextIndex >= validTopics.size()) {
            i = 0;
            nextIndex = i;
        } else {
            nextIndex = i + 1;
        }
        List<String> list = Collects.asList(validTopics);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > i) {
            return list.get(i);
        }
        return null;
    }
}
