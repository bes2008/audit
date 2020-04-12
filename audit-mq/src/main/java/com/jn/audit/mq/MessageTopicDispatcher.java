package com.jn.audit.mq;

import com.jn.langx.lifecycle.Destroyable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;

import java.util.Map;

public class MessageTopicDispatcher implements Destroyable, Initializable {
    private final Map<String, MessageTopic> topicMap = Collects.emptyHashMap();

    public void registerTopic(MessageTopic messageTopic) {
        topicMap.put(messageTopic.getName(), messageTopic);
    }

    public void unregisterTopic(String name) {
        topicMap.remove(name);
    }

    @Override
    public void init() throws InitializationException {

    }

    @Override
    public void destroy() {
        Collects.forEach(topicMap, new Consumer2<String, MessageTopic>() {
            @Override
            public void accept(String topicName, MessageTopic topic) {

            }
        });
    }
}
