package com.jn.audit.mq.allocator;

import com.jn.audit.mq.TopicAllocator;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.random.ThreadLocalRandom;

import java.util.LinkedHashSet;
import java.util.List;

public class RandomTopicAllocator<M> implements TopicAllocator<M> {
    public final List<String> topics = Collects.newArrayList();

    public RandomTopicAllocator(String... topics) {
        this.topics.addAll(new LinkedHashSet<String>(Collects.asList(topics)));
    }

    @Override
    public String apply(M input) {
        if (topics.isEmpty()) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(0, topics.size());
        return topics.get(index);
    }
}
