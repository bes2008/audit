package com.jn.audit.spring.boot.autoconfigure;

import com.jn.audit.core.*;
import com.jn.audit.core.filter.MethodAuditAnnotationFilter;
import com.jn.audit.core.operation.OperationDefinitionParserRegistry;
import com.jn.audit.core.operation.OperationIdGenerator;
import com.jn.audit.core.operation.OperationParametersExtractor;
import com.jn.audit.core.operation.method.OperationMethodExtractor;
import com.jn.audit.mq.MessageTopicDispatcher;
import com.jn.audit.mq.consumer.DebugConsumer;
import com.jn.audit.servlet.ServletAuditEventExtractor;
import com.jn.audit.servlet.ServletAuditRequest;
import com.jn.audit.servlet.ServletHttpParametersExtractor;
import com.jn.langx.configuration.MultipleLevelConfigurationRepository;
import com.jn.langx.util.function.Function2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

@ConditionalOnProperty(value = "auditor.enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class AuditAutoConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(name = "servletHttpParametersExtractor")
    @Bean("servletHttpParametersExtractor")
    public ServletHttpParametersExtractor servletHttpParametersExtractor() {
        return new ServletHttpParametersExtractor();
    }

    @ConditionalOnWebApplication
    @Bean(name = "operationMethodExtractor")
    @ConditionalOnMissingBean(name = {"operationMethodExtractor"})
    public OperationMethodExtractor<HttpServletRequest> operationMethodExtractor(
            @Autowired @Qualifier("multipleLevelOperationDefinitionRepository")
                    MultipleLevelConfigurationRepository multipleLevelConfigurationRepository,
            ObjectProvider<OperationIdGenerator<HttpServletRequest, Method>> operationIdGenerators,
            @Autowired @Qualifier("servletHttpParametersExtractor")
                    OperationParametersExtractor<HttpServletRequest, Method> httpOperationParametersExtractor,
            @Autowired @Qualifier("operationDefinitionParserRegistry")
                    OperationDefinitionParserRegistry definitionParserRegistry
    ) {
        OperationMethodExtractor<HttpServletRequest> operationExtractor = new OperationMethodExtractor<>();
        operationExtractor.setOperationDefinitionRepository(multipleLevelConfigurationRepository);
        operationExtractor.setOperationIdGenerators(operationIdGenerators.orderedStream().collect(Collectors.toList()));
        operationExtractor.setOperationParametersExtractor(httpOperationParametersExtractor);
        operationExtractor.setOperationParserRegistry(definitionParserRegistry);
        return operationExtractor;
    }

    @Bean(name = "servletAuditEventExtractor")
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean(name = {"servletAuditEventExtractor"})
    public ServletAuditEventExtractor servletAuditEventExtractor(
            @Autowired @Qualifier("operationMethodExtractor")
                    OperationMethodExtractor<HttpServletRequest> operationMethodExtractor) {
        ServletAuditEventExtractor auditEventExtractor = new ServletAuditEventExtractor();
        auditEventExtractor.setOperationExtractor(operationMethodExtractor);
        return auditEventExtractor;
    }

    @ConditionalOnWebApplication
    @Bean(name = "auditRequestFactory")
    @ConditionalOnMissingBean(name = "auditRequestFactory")
    public Function2<HttpServletRequest, Method, ServletAuditRequest> servletAuditRequestFactory() {
        return new Function2<HttpServletRequest, Method, ServletAuditRequest>() {
            @Override
            public ServletAuditRequest apply(HttpServletRequest request, Method method) {
                return new ServletAuditRequest(request, method);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "debugConsumer", value = {DebugConsumer.class})
    public DebugConsumer debugConsumer() {
        return new DebugConsumer();
    }


    @Bean(name = "auditor")
    @ConditionalOnMissingBean(name = "auditor")
    @Autowired
    public Auditor auditor(AuditProperties auditSettings,
                           MessageTopicDispatcher dispatcher,
                           AuditEventExtractor auditEventExtractor,
                           DebugConsumer debugConsumer
    ) {
        Auditor auditor = new SimpleAuditorFactory<AuditSettings>() {
            @Override
            protected Function2 getAuditRequestFactory() {
                Function2 auditRequestFactory = (Function2) applicationContext.getBean("auditRequestFactory");
                if (auditRequestFactory == null) {
                    return super.getAuditRequestFactory();
                }
                return auditRequestFactory;
            }

            @Override
            protected void initBeforeFilterChain(AuditRequestFilterChain chain, AuditSettings settings) {
                chain.addFilter(new MethodAuditAnnotationFilter<>());
            }

            @Override
            protected MessageTopicDispatcher getMessageTopicDispatcher(AuditSettings settings) {
                return dispatcher;
            }


        }.get(auditSettings);
        auditor.setAuditEventExtractor(auditEventExtractor);
        if (auditSettings.isDebugConsumerEnabled()) {
            dispatcher.subscribe("*", debugConsumer);
        }
        return auditor;
    }

    @Bean
    @ConditionalOnMissingBean(value = {MessageTopicDispatcher.class})
    @Autowired
    public MessageTopicDispatcher messageTopicDispatcher(AuditProperties auditSettings) {
        return new MessageTopicDispatcher();
    }


}
