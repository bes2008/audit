package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.core.AuditEventExtractor;
import com.jn.audit.core.Auditor;
import com.jn.audit.core.SimpleAuditorFactory;
import com.jn.audit.core.operation.OperationDefinitionParserRegistry;
import com.jn.audit.core.operation.OperationIdGenerator;
import com.jn.audit.core.operation.OperationParametersExtractor;
import com.jn.audit.core.operation.method.OperationAnnotationParser;
import com.jn.audit.core.operation.method.OperationMethodExtractor;
import com.jn.audit.mq.MessageTopicDispatcher;
import com.jn.audit.mq.consumer.DebugConsumer;
import com.jn.audit.servlet.ServletAuditEventExtractor;
import com.jn.audit.servlet.ServletAuditRequest;
import com.jn.audit.servlet.ServletHttpParametersExtractor;
import com.jn.audit.servlet.ServletUrlOperationIdGenerator;
import com.jn.langx.configuration.MultipleLevelConfigurationRepository;
import com.jn.langx.util.function.Function2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties({AuditProperties.class})
public class AuditConfig {
    @Autowired
    private MessageTopicDispatcher dispatcher;

    @Bean("servletHttpParametersExtractor")
    public ServletHttpParametersExtractor servletHttpParametersExtractor() {
        return new ServletHttpParametersExtractor();
    }

    @Bean
    public OperationMethodExtractor<HttpServletRequest> operationMethodExtractor(
            @Autowired @Qualifier("multipleLevelOperationDefinitionRepository")
            MultipleLevelConfigurationRepository multipleLevelConfigurationRepository,
            ObjectProvider<OperationIdGenerator<HttpServletRequest, Method>> operationIdGenerators,
            @Autowired @Qualifier("servletHttpParametersExtractor")
            OperationParametersExtractor<HttpServletRequest,Method> httpOperationParametersExtractor,
            @Autowired @Qualifier("operationDefinitionParserRegistry")
            OperationDefinitionParserRegistry definitionParserRegistry
            ){
        OperationMethodExtractor<HttpServletRequest> operationExtractor = new OperationMethodExtractor<>();
        operationExtractor.setOperationDefinitionRepository(multipleLevelConfigurationRepository);
        operationExtractor.setOperationIdGenerators(operationIdGenerators.orderedStream().collect(Collectors.toList()));
        operationExtractor.setOperationParametersExtractor(httpOperationParametersExtractor);
        operationExtractor.setOperationParserRegistry(definitionParserRegistry);
        return operationExtractor;
    }

    @Bean
    public ServletAuditEventExtractor servletAuditEventExtractor(
            @Autowired
                    OperationMethodExtractor<HttpServletRequest> operationMethodExtractor) {
        ServletAuditEventExtractor auditEventExtractor = new ServletAuditEventExtractor();
        auditEventExtractor.setOperationExtractor(operationMethodExtractor);
        return auditEventExtractor;
    }


    @Bean
    @Autowired
    public Auditor auditor(AuditProperties auditSettings,
                           MessageTopicDispatcher dispatcher,
                           DebugConsumer debugConsumer,
                           AuditEventExtractor auditEventExtractor) {
        Auditor auditor = new SimpleAuditorFactory<AuditProperties>() {
            @Override
            protected Function2 getAuditRequestFactory() {
                return new Function2<HttpServletRequest, Method, ServletAuditRequest>() {
                    @Override
                    public ServletAuditRequest apply(HttpServletRequest request, Method method) {
                        return new ServletAuditRequest(request, method);
                    }
                };
            }

            @Override
            protected MessageTopicDispatcher getMessageTopicDispatcher(AuditProperties settings) {
                return dispatcher;
            }


        }.get(auditSettings);
        auditor.setAuditEventExtractor(auditEventExtractor);

        dispatcher.subscribe("*", debugConsumer);

        dispatcher.startup();
        return auditor;
    }

    @Bean
    public MessageTopicDispatcher messageTopicDispatcher() {
        return new MessageTopicDispatcher();
    }

    @Bean
    public DebugConsumer debugConsumer() {
        return new DebugConsumer();
    }


}
