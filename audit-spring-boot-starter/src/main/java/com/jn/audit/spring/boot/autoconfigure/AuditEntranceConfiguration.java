package com.jn.audit.spring.boot.autoconfigure;


import com.jn.dmmq.core.MessageTopicDispatcher;
import com.jn.audit.spring.webmvc.AuditHttpHandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

@Configuration
public class AuditEntranceConfiguration implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(AuditEntranceConfiguration.class);


    private MessageTopicDispatcher dispatcher;

    @Autowired
    public void setDispatcher(MessageTopicDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(name = "auditHttpHandlerInterceptor")
    public AuditHttpHandlerInterceptor auditHttpHandlerInterceptor() {
        return new AuditHttpHandlerInterceptor();
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("Application refresh finish");
        dispatcher.startup();
    }
}
