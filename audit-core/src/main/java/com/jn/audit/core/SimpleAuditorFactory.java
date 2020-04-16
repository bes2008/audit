package com.jn.audit.core;

import com.jn.audit.core.model.AuditEvent;
import com.jn.audit.mq.*;
import com.jn.audit.mq.allocator.RoundRobinTopicAllocator;
import com.jn.audit.mq.event.TopicEvent;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.local.SimpleEventPublisher;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import com.lmax.disruptor.WaitStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleAuditorFactory<Settings extends AuditSettings> implements AuditorFactory<Settings> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleAuditorFactory.class);

    protected WaitStrategy findDefaultWaitStrategy(Settings settings) {
        String name = settings.getConsumerWaitStrategy();
        BuiltinWaitStrategyFactory factory = new BuiltinWaitStrategyFactory();
        if (Strings.isEmpty(name)) {
            name = "blocking";
        }
        if (factory.isBuiltin(name)) {
            return factory.get(name);
        }
        try {
            Class clazz = ClassLoaders.loadClass(name, SimpleAuditorFactory.class.getClassLoader());
            return Reflects.<WaitStrategy>newInstance(clazz);
        } catch (Throwable ex) {
            logger.warn("error when load a class or create instance: {}", name);
        }
        return factory.get("blocking");
    }

    protected MessageTranslator findMessageTranslator(Settings settings) {
        String className = settings.getMessageTranslator();
        if (Strings.isEmpty(className)) {
            className = Reflects.getFQNClassName(DefaultMessageTranslator.class);
        }
        try {
            Class clazz = ClassLoaders.loadClass(className, SimpleAuditorFactory.class.getClassLoader());
            return Reflects.<MessageTranslator>newInstance(clazz);
        } catch (Throwable ex) {
            logger.warn("error when load a class or create instance: {}", className);
            return Reflects.newInstance(DefaultMessageTranslator.class);
        }
    }


    protected TopicAllocator findTopicAllocator(Settings settings) {
        String className = settings.getTopicAllocator();
        if (Strings.isEmpty(className)) {
            className = Reflects.getFQNClassName(RoundRobinTopicAllocator.class);
        }
        try {
            Class clazz = ClassLoaders.loadClass(className, SimpleAuditorFactory.class.getClassLoader());
            return Reflects.<TopicAllocator>newInstance(clazz);
        } catch (Throwable ex) {
            logger.warn("error when load a class or create instance: {}", className);
            return Reflects.newInstance(RoundRobinTopicAllocator.class);
        }
    }

    protected Executor getDefaultExecutor(Settings settings) {
        if (settings.getExecutor() != null) {
            return settings.getExecutor();
        }
        if (settings.isAsyncMode() || Collects.anyMatch(settings.getTopics(), new Predicate<MessageTopicConfiguration>() {
            @Override
            public boolean test(MessageTopicConfiguration configuration) {
                return configuration.getExecutor() == null;
            }
        })) {
            return new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new CommonThreadFactory("Auditor", true));
        }
        return null;
    }

    protected EventPublisher findEventPublisher(Settings settings) {
        return new SimpleEventPublisher();
    }

    protected void initFilterChain(AuditRequestFilterChain chain, Settings settings) {

    }

    @Override
    public Auditor get(Settings settings) {
        Auditor auditor = new Auditor();
        auditor.setAsyncAudit(settings.isAsyncMode());

        // event publisher
        EventPublisher eventPublisher = findEventPublisher(settings);

        // executor
        final Executor defaultExecutor = getDefaultExecutor(settings);
        // dispatcher
        final MessageTopicDispatcher dispatcher = new MessageTopicDispatcher();
        // message translator
        final MessageTranslator translator = findMessageTranslator(settings);

        // topics
        final WaitStrategy defaultWaitStrategy = findDefaultWaitStrategy(settings);
        List<MessageTopicConfiguration> topicConfigs = settings.getTopics();
        Collects.forEach(topicConfigs, new Consumer<MessageTopicConfiguration>() {
            @Override
            public void accept(MessageTopicConfiguration topicConfig) {
                MessageTopic topic = new MessageTopic();
                topic.setName(topicConfig.getName());
                if (topicConfig.getRingBufferSize() < 2) {
                    topicConfig.setRingBufferSize(8096);
                }
                if (topicConfig.getExecutor() == null) {
                    topicConfig.setExecutor(defaultExecutor);
                }
                if (topicConfig.getWaitStrategy() == null) {
                    topicConfig.setWaitStrategy(defaultWaitStrategy);
                }
                if (topicConfig.getMessageTranslator() == null) {
                    topicConfig.setMessageTranslator(translator);
                }
                topic.setConfiguration(topicConfig);
                dispatcher.registerTopic(topic);
            }
        });

        // topic allocator
        TopicAllocator topicAllocator = findTopicAllocator(settings);
        eventPublisher.addEventListener(TopicEvent.DOMAIN, topicAllocator);

        // producer
        SimpleProducer<AuditEvent> simpleProducer = new SimpleProducer<AuditEvent>();
        simpleProducer.setMessageTopicDispatcher(dispatcher);
        simpleProducer.setTopicAllocator(topicAllocator);

        // executor
        if (auditor.getExecutor() == null) {
            auditor.setExecutor(defaultExecutor);
        }

        // filter chain
        AuditRequestFilterChain filterChain = new AuditRequestFilterChain();
        initFilterChain(filterChain, settings);
        auditor.setFilterChain(filterChain);

        return auditor;
    }
}
