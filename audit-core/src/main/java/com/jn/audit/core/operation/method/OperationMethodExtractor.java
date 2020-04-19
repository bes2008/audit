package com.jn.audit.core.operation.method;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Operation;
import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.operation.OperationDefinitionParserRegistry;
import com.jn.audit.core.operation.OperationExtractor;
import com.jn.audit.core.operation.OperationIdGenerator;
import com.jn.audit.core.operation.OperationParametersExtractor;
import com.jn.audit.core.operation.repository.OperationDefinitionRepository;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Holder;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 根据 method 来获取 Operation
 * <p>
 * 重点是根据 method 来获取 OperationDefinition
 * 根据method查找operation definition的步骤：
 * 1. get operation definition from cache
 * 2. parse method
 * 2.1) parse annotations based on parser registry
 * 2.2) parse from configuration file
 * 2.2.1) using custom operation id generator
 * 2.2.2) using method full name (exclude parameters)
 *
 * @param <AuditedRequest>
 */
public class OperationMethodExtractor<AuditedRequest> implements OperationExtractor<AuditedRequest, Method>, Initializable {
    private volatile boolean inited = false;
    private Cache<Method, OperationDefinition> methodOperationDefinitionCache;

    private OperationDefinitionParserRegistry operationParserRegistry;

    private List<OperationIdGenerator<AuditedRequest, Method>> operationIdGenerators;

    private OperationDefinitionRepository operationDefinitionRepository;

    private OperationParametersExtractor<AuditedRequest, Method> operationParametersExtractor;

    private Map<String, List<? extends Serializable>> extractOperationParameters(final AuditRequest<AuditedRequest, Method> wrappedRequest) {
        return Emptys.isNull(operationParametersExtractor) ? null : operationParametersExtractor.get(wrappedRequest);
    }

    @Override
    public void init() throws InitializationException {
        if (methodOperationDefinitionCache == null) {
            methodOperationDefinitionCache = CacheBuilder.<Method, OperationDefinition>newBuilder()
                    .initialCapacity(100)
                    .capacityHeightWater(0.75f)
                    .softKey(true)
                    .build();
        }
        inited = true;
    }

    @Override
    public Operation get(final AuditRequest<AuditedRequest, Method> wrappedRequest) {
        if (!inited) {
            init();
        }
        Operation operation = new Operation();
        OperationDefinition definition = findOperationDefinition(wrappedRequest);
        operation.setDefinition(definition);
        return operation;
    }

    public OperationDefinition findOperationDefinition(final AuditRequest<AuditedRequest, Method> wrappedRequest) {
        final Method method = wrappedRequest.getRequestContext();
        // step 1: get operation definition from cache
        final Holder<OperationDefinition> operationDefinition = new Holder<OperationDefinition>(methodOperationDefinitionCache.get(method));

        if (operationDefinition.isNull()) {
            // step 2: parse method
            // step 2.1 parse annotations based on parser registry
            Collects.forEach(operationParserRegistry.getAnnotationParsers(), new Predicate<OperationMethodAnnotationParser<? extends Annotation>>() {
                @Override
                public boolean test(OperationMethodAnnotationParser<? extends Annotation> parser) {
                    return Reflects.getAnnotation(method, parser.getAnnotation()) != null;
                }
            }, new Consumer<OperationMethodAnnotationParser<? extends Annotation>>() {
                @Override
                public void accept(OperationMethodAnnotationParser<? extends Annotation> parser) {
                    operationDefinition.set(parser.parse(method));
                }
            }, new Predicate<OperationMethodAnnotationParser<? extends Annotation>>() {
                @Override
                public boolean test(OperationMethodAnnotationParser<? extends Annotation> parser) {
                    return !operationDefinition.isNull();
                }
            });

            // 2.2) parse from configuration file
            if (operationDefinition.isNull()) {
                // 2.2.1 using custom operation id generator
                Collects.forEach(operationIdGenerators, new Consumer<OperationIdGenerator<AuditedRequest, Method>>() {
                    @Override
                    public void accept(OperationIdGenerator<AuditedRequest, Method> generator) {
                        String operationDefinitionId = generator.get(wrappedRequest);
                        operationDefinition.set(operationDefinitionRepository.getById(operationDefinitionId));
                    }
                }, new Predicate<OperationIdGenerator<AuditedRequest, Method>>() {
                    @Override
                    public boolean test(OperationIdGenerator<AuditedRequest, Method> value) {
                        return !operationDefinition.isNull();
                    }
                });
            }

            if (operationDefinition.isNull()) {
                // 2.2.2 using method full name (exclude parameters)
                String operationDefinitionId = Reflects.getMethodString(method);
                operationDefinition.set(operationDefinitionRepository.getById(operationDefinitionId));
            }
        }
        return operationDefinition.get();
    }

    public Cache<Method, OperationDefinition> getMethodOperationDefinitionCache() {
        return methodOperationDefinitionCache;
    }

    public void setMethodOperationDefinitionCache(Cache<Method, OperationDefinition> methodOperationDefinitionCache) {
        this.methodOperationDefinitionCache = methodOperationDefinitionCache;
    }

    public OperationDefinitionParserRegistry getOperationParserRegistry() {
        return operationParserRegistry;
    }

    public void setOperationParserRegistry(OperationDefinitionParserRegistry operationParserRegistry) {
        this.operationParserRegistry = operationParserRegistry;
    }

    public List<OperationIdGenerator<AuditedRequest, Method>> getOperationIdGenerators() {
        return operationIdGenerators;
    }

    public void setOperationIdGenerators(List<OperationIdGenerator<AuditedRequest, Method>> operationIdGenerators) {
        this.operationIdGenerators = operationIdGenerators;
    }

    public void addOperationIdGenerator(OperationIdGenerator<AuditedRequest, Method> generator) {
        this.operationIdGenerators.add(generator);
    }

    public OperationDefinitionRepository getOperationDefinitionRepository() {
        return operationDefinitionRepository;
    }

    public void setOperationDefinitionRepository(OperationDefinitionRepository operationDefinitionRepository) {
        this.operationDefinitionRepository = operationDefinitionRepository;
    }

    public OperationParametersExtractor<AuditedRequest, Method> getOperationParametersExtractor() {
        return operationParametersExtractor;
    }

    public void setOperationParametersExtractor(OperationParametersExtractor<AuditedRequest, Method> operationParametersExtractor) {
        this.operationParametersExtractor = operationParametersExtractor;
    }
}
