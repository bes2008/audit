package com.jn.audit.mq;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Just send message to the 'DEFAULT' topic if the topicName is not specified
 *
 * @param <M>
 */
public class SimpleProducer<M> implements Producer<M> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
    private TopicAllocator<M> topicAllocator;

    @Override
    public void setTopicAllocator(TopicAllocator<M> topicAllocator) {
        this.topicAllocator = topicAllocator;
    }

    @Override
    public void publish(M message) {
        publish(null, message);
    }

    @Override
    public void publish(@Nullable String topicName, @NonNull M message) {
        Preconditions.checkNotNull(message);
        MessageTopicDispatcher dispatcher = MessageTopicDispatcher.getInstance();
        topicName = getTopic(topicName, message);
        if (Strings.isEmpty(topicName)) {
            logger.warn("not found an invalid topic for the message: {}", message);
            return;
        }
        dispatcher.publish(topicName, message);
    }

    private String getTopic(String topicName, M message) {
        if (Strings.isEmpty(topicName)) {
            return topicAllocator.apply(message);
        }
        return topicName;
    }
}
