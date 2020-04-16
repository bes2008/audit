package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.core.Auditor;
import com.jn.audit.core.SimpleAuditorFactory;
import com.jn.audit.examples.springmvcdemo.common.audit.DemoAuditExtractor;
import com.jn.audit.mq.MessageTopicDispatcher;
import com.jn.audit.mq.consumer.DebugConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AuditProperties.class})
public class AuditConfig {
    @Bean
    public Auditor auditor(@Autowired AuditProperties auditSettings, @Autowired DemoAuditExtractor auditExtractor) {
        Auditor auditor = new SimpleAuditorFactory<AuditProperties>().get(auditSettings);
        auditor.setAuditEventExtractor(auditExtractor);
        return auditor;
    }

    @Bean
    public MessageTopicDispatcher messageTopicDispatcher(@Autowired Auditor auditor) {
        return auditor.getProducer().getMessageTopicDispatcher();
    }

    @Autowired
    private void initTopic(MessageTopicDispatcher dispatcher, DebugConsumer debugConsumer) {
        dispatcher.subscribe("*", debugConsumer);

        dispatcher.startup();
    }

    @Bean
    public DebugConsumer debugConsumer() {
        return new DebugConsumer();
    }


}
