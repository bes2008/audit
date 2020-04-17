package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.core.Auditor;
import com.jn.audit.core.SimpleAuditorFactory;
import com.jn.audit.examples.springmvcdemo.common.audit.DemoAuditExtractor;
import com.jn.audit.mq.MessageTopicDispatcher;
import com.jn.audit.mq.TopicAllocator;
import com.jn.audit.mq.allocator.DefaultTopicAllocator;
import com.jn.audit.mq.consumer.DebugConsumer;
import com.jn.audit.servlet.ServletAuditRequest;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.function.Function2;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.reflect.Reflects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Configuration
@EnableConfigurationProperties({AuditProperties.class})
public class AuditConfig {
    @Bean
    public Auditor auditor(@Autowired AuditProperties auditSettings, @Autowired DemoAuditExtractor auditExtractor) {
        Auditor auditor = new SimpleAuditorFactory<AuditProperties>(){
            @Override
            protected Function2 getAuditRequestFactory() {
                return new Function2<HttpServletRequest, Method, ServletAuditRequest>() {
                    @Override
                    public ServletAuditRequest apply(HttpServletRequest request, Method method) {
                        return new ServletAuditRequest(request, method);
                    }
                };
            }
        }.get(auditSettings);
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
