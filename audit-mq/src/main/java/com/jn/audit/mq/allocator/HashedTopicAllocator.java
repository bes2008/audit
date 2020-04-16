package com.jn.audit.mq.allocator;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Function;

import java.util.List;

public class HashedTopicAllocator<M> extends AbstractMultipleCandidateTopicAllocator<M> {

    private final Function<M, Integer> hasher;

    public HashedTopicAllocator(List<String> topics, Function<M, Integer> hasher) {
        super(topics);
        this.hasher = hasher;
    }

    @Override
    public String apply(M message) {
        List<String> topics = Collects.asList(validTopics);
        if (topics.isEmpty()) {
            return null;
        }
        return topics.get(hasher.apply(message) % topics.size());
    }
}
