package com.jn.audit.mq.allocator;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.random.ThreadLocalRandom;

import java.util.List;

public class RandomTopicAllocator<M> extends AbstractMultipleCandidateTopicAllocator<M> {

    public RandomTopicAllocator(List<String> topics) {
        super(topics);
    }

    @Override
    public String apply(M input) {
        if (validTopics.isEmpty()) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(0, validTopics.size());
        return Collects.asList(validTopics).get(index);
    }


}
