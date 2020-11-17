package com.jn.audit.spring.boot.autoconfigure;

import com.jn.audit.core.resource.ResourceMethodInvocationExtractor;
import com.jn.audit.core.resource.idresource.EntityLoaderDispatcher;
import com.jn.audit.core.resource.idresource.EntityLoaderRegistry;
import com.jn.audit.core.resource.idresource.LoadingEntityIdResourceExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceExtractAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EntityLoaderRegistry.class)
    public EntityLoaderRegistry entityLoaderRegistry() {
        return new EntityLoaderRegistry();
    }

    @Bean
    @ConditionalOnMissingBean(EntityLoaderDispatcher.class)
    @Autowired
    public EntityLoaderDispatcher entityLoaderDispatcher(EntityLoaderRegistry registry) {
        EntityLoaderDispatcher dispatcher = new EntityLoaderDispatcher();
        dispatcher.setRegistry(registry);
        return dispatcher;
    }

    @Bean
    @ConditionalOnMissingBean(LoadingEntityIdResourceExtractor.class)
    @Autowired
    public LoadingEntityIdResourceExtractor loadingEntityIdResourceExtractor(EntityLoaderDispatcher dispatcher) {
        LoadingEntityIdResourceExtractor extractor = new LoadingEntityIdResourceExtractor();
        extractor.setEntityLoader(dispatcher);
        return extractor;
    }

    @Bean
    @ConditionalOnMissingBean
    @Autowired
    public ResourceMethodInvocationExtractor resourceMethodInvocationExtractor(LoadingEntityIdResourceExtractor idResourceExtractor){
        ResourceMethodInvocationExtractor resourceMethodInvocationExtractor = new ResourceMethodInvocationExtractor();
        resourceMethodInvocationExtractor.setIdResourceExtractor(idResourceExtractor);
        return resourceMethodInvocationExtractor;
    }
}
