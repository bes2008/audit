package com.jn.audit.examples.springmvcdemo.common.config;

import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.operation.OperationDefinitionParserRegistry;
import com.jn.audit.core.operation.method.OperationAnnotationParser;
import com.jn.audit.core.operation.method.OperationMethodAnnotationDefinitionParser;
import com.jn.audit.core.operation.repository.OperationDefinitionLoader;
import com.jn.audit.core.operation.repository.OperationDefinitionRepository;
import com.jn.audit.core.operation.repository.OperationRepositoryParser;
import com.jn.audit.core.operation.repository.YamlOperationDefinitionLoader;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.langx.configuration.MultipleLevelConfigurationRepository;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import com.jn.langx.util.timing.timer.Timer;
import com.jn.langx.util.timing.timer.WheelTimers;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.function.Consumer;

@Configuration
public class OperationConfig {

    @Bean
    public Timer timer() {
        return WheelTimers.newHashedWheelTimer(new CommonThreadFactory());
    }

    @Bean("operationDefinitionCache")
    public Cache<String, OperationDefinition> operationDefinitionCache(@Autowired Timer timer) {
        return CacheBuilder.<String, OperationDefinition>newBuilder()
                .initialCapacity(500)
                .capacityHeightWater(0.95f)
                .timer(timer)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    @Bean
    public OperationDefinitionLoader yamlOperationDefinitionLoader(@Value("operation.definition.location") String location) {
        YamlOperationDefinitionLoader loader = new YamlOperationDefinitionLoader();
        loader.setDefinitionFilePath(location);
        return loader;
    }

    @Bean("yamlOperationDefinitionRepository")
    public OperationDefinitionRepository yamlOperationDefinitionRepository(
            @Autowired @Qualifier("operationDefinitionCache")
                    Cache<String, OperationDefinition> operationDefinitionCache,
            @Autowired
                    Timer timer,
            @Autowired OperationDefinitionLoader yamlLoader
    ) {
        OperationDefinitionRepository repository = new OperationDefinitionRepository();
        repository.setName("Operation-Definition");
        repository.setCache(operationDefinitionCache);
        repository.setTimer(timer);
        repository.setConfigurationLoader(yamlLoader);
        repository.setReloadIntervalInSeconds(60);
        return repository;
    }

    @Bean("multipleLevelOperationDefinitionRepository")
    public MultipleLevelConfigurationRepository multipleLevelOperationDefinitionRepository(
            @Autowired @Qualifier("yamlOperationDefinitionRepository")
                    OperationDefinitionRepository yamlRepository
    ) {
        MultipleLevelConfigurationRepository repository = new MultipleLevelConfigurationRepository();
        repository.addRepository(yamlRepository);
        return repository;
    }

    @Bean
    @Order(0)
    public OperationAnnotationParser operationAnnotationParser(){
        return new OperationAnnotationParser();
    }

    @Autowired
    @Bean("operationDefinitionParserRegistry")
    public OperationDefinitionParserRegistry operationDefinitionParserRegistry(
            ObjectProvider<OperationMethodAnnotationDefinitionParser> methodAnnotationDefinitionParsers,
            ObjectProvider<OperationRepositoryParser> repositoryDefinitionParsers
    ){
        OperationDefinitionParserRegistry registry = new OperationDefinitionParserRegistry();
        methodAnnotationDefinitionParsers.orderedStream().forEach(new Consumer<OperationMethodAnnotationDefinitionParser>() {
            @Override
            public void accept(OperationMethodAnnotationDefinitionParser operationMethodAnnotationDefinitionParser) {
                registry.registry(operationMethodAnnotationDefinitionParser);
            }
        });
        repositoryDefinitionParsers.orderedStream().forEach(new Consumer<OperationRepositoryParser>() {
            @Override
            public void accept(OperationRepositoryParser operationRepositoryParser) {
                registry.registry(operationRepositoryParser);
            }
        });
        return registry;
    }


}
