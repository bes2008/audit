package com.jn.audit.core;

import com.jn.audit.mq.MessageTopic;
import com.jn.audit.mq.MessageTopicConfiguration;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

import java.util.List;

public class SimpleAuditorFactory implements AuditorFactory<AuditSettings> {
    @Override
    public Auditor get(AuditSettings settings) {
        Auditor auditor = new  Auditor();
        auditor.setAsyncAudit(settings.isAsyncMode());

        List<MessageTopicConfiguration> topicConfigs = settings.getTopics();
        Collects.forEach(topicConfigs, new Consumer<MessageTopicConfiguration>() {
            @Override
            public void accept(MessageTopicConfiguration topicConfig) {
                MessageTopic topic = new MessageTopic();
                topic.setConfiguration(topicConfig);
            }
        });

        return auditor;
    }
}
