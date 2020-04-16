package com.jn.audit.mq;

import com.jn.langx.util.Objects;
import com.jn.langx.util.Strings;

/**
 * Just send message to the 'DEFAULT' topic if the topicName is not specified
 *
 * @param <M>
 */
public class DefaultProducer<M> implements Producer<M> {

    @Override
    public String apply(M message) {
        return "DEFAULT";
    }

    @Override
    public void publish(String topicName, M message) {
        if (Objects.isNotNull(message)) {
            MessageTopicDispatcher dispatcher = MessageTopicDispatcher.getInstance();
            dispatcher.publish(getTopic(topicName, message), message);
        }
    }

    private String getTopic(String topicName, M message) {
        if (Strings.isEmpty(topicName)) {
            return apply(message);
        }
        return topicName;
    }
}
