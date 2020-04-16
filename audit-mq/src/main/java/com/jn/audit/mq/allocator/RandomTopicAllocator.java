package com.jn.audit.mq.allocator;

import com.jn.audit.mq.MessageTopic;
import com.jn.audit.mq.TopicAllocator;
import com.jn.audit.mq.event.TopicEvent;
import com.jn.audit.mq.event.TopicEventType;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.random.ThreadLocalRandom;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class RandomTopicAllocator<M> implements TopicAllocator<M> {
    private final Set<String> watchedTopics = Collects.newHashSet();
    private final Set<String> validTopics = new CopyOnWriteArraySet<String>();

    public RandomTopicAllocator(List<String> topics) {
        this.watchedTopics.addAll(topics);
    }

    public RandomTopicAllocator(String... topics) {
        this.watchedTopics.addAll(Collects.asList(topics));
    }

    @Override
    public String apply(M input) {
        if (validTopics.isEmpty()) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(0, validTopics.size());
        return Collects.asList(validTopics).get(index);
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
