package com.jn.audit.mq;

import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.Objects;
import com.jn.langx.util.collection.Collects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Singleton
public class MessageTopicDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(MessageTopicDispatcher.class);
    private static final MessageTopicDispatcher dispatcher = new MessageTopicDispatcher();
    private final Map<String, MessageTopic> topicMap = Collects.emptyHashMap();

    private MessageTopicDispatcher() {
    }

    public static MessageTopicDispatcher getInstance() {
        return dispatcher;
    }

    public void registerTopic(MessageTopic messageTopic) {
        topicMap.put(messageTopic.getName(), messageTopic);
    }

    public void unregisterTopic(String name) {
        topicMap.remove(name);
    }

    public void publish(String topicName, Object message) {
        MessageTopic topic = topicMap.get(topicName);
        if (Objects.isNull(topic)) {
            logger.warn("Can't find a topic : {}", topicName);
        } else {
            topic.publish(message);
        }
    }

    public <M> void subscribe(String topicName, Consumer<M> consumer, String... dependencies) {
        MessageTopic topic = topicMap.get(topicName);
        if (Objects.isNull(topic)) {
            logger.warn("Can't find a topic : {}", topicName);
        } else {
            topic.subscribe(consumer, dependencies);
        }
    }

}
