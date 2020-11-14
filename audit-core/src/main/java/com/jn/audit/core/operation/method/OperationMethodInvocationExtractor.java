package com.jn.audit.core.operation.method;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Operation;
import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.operation.OperationDefinitionParserRegistry;
import com.jn.audit.core.operation.OperationExtractor;
import com.jn.audit.core.operation.OperationIdGenerator;
import com.jn.audit.core.operation.OperationParametersExtractor;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.langx.configuration.MultipleLevelConfigurationRepository;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Holder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 根据 method 来获取 Operation
 * <p>
 * <pre>
 * 重点是根据 method 来获取 OperationDefinition
 * 根据method查找operation definition的步骤：
 *  1. get operation definition from cache
 *  2. parse method
 *      2.1) parse annotations based on parser registry
 *      2.2) parse from configuration file
 *          2.2.1) using custom operation id generator
 *          2.2.2) using method full name (exclude parameters)
 * </pre>
 * </p>
 *
 * @param <AuditedRequest>
 */
public class OperationMethodInvocationExtractor<AuditedRequest> implements OperationExtractor<AuditedRequest, MethodInvocation>, Initializable {
    private volatile boolean inited = false;
    /**
     * value: operation definition id
     */
    private Cache<Method, String> methodOperationDefinitionCache;

    private OperationDefinitionParserRegistry operationParserRegistry;

    private List<OperationIdGenerator<AuditedRequest, MethodInvocation>> operationIdGenerators;

    private MultipleLevelConfigurationRepository operationDefinitionRepository;

    private OperationParametersExtractor<AuditedRequest, MethodInvocation> operationParametersExtractor;

    private Map<String, Object> extractOperationParameters(final AuditRequest<AuditedRequest, MethodInvocation> wrappedRequest) {
        return Emptys.isNull(operationParametersExtractor) ? null : operationParametersExtractor.get(wrappedRequest);
    }

    @Override
    public void init() throws InitializationException {
        if (methodOperationDefinitionCache == null) {
            methodOperationDefinitionCache = CacheBuilder.<Method, String>newBuilder()
                    .initialCapacity(100)
                    .capacityHeightWater(0.75f)
                    .softKey(true)
                    .build();
        }
        inited = true;
    }

    @Override
    public Operation get(final AuditRequest<AuditedRequest, MethodInvocation> wrappedRequest) {
        if (!inited) {
            init();
        }
        Operation operation = new Operation();
        OperationDefinition definition = findOperationDefinition(wrappedRequest);
        operation.setDefinition(definition);
        Map<String, Object> parameters = extractOperationParameters(wrappedRequest);
        operation.setParameters(parameters);
        return operation;
    }

    private OperationDefinition getOperationDefinitionByCachedId(Method method) {
        String id = methodOperationDefinitionCache.get(method);
        if (Emptys.isNotEmpty(id)) {
            return (OperationDefinition) operationDefinitionRepository.getById(id);
        }
        return null;
    }

    public OperationDefinition findOperationDefinition(final AuditRequest<AuditedRequest, MethodInvocation> wrappedRequest) {
        MethodInvocation methodInvocation = wrappedRequest.getRequestContext();
        final Method method = methodInvocation.getJoinPoint();
        // step 1: get operation definition from cache
        final Holder<OperationDefinition> operationDefinition = new Holder<OperationDefinition>(getOperationDefinitionByCachedId(method));

        if (operationDefinition.isNull()) {
            // step 2: parse method
            // step 2.1 parse annotations based on parser registry
            Collects.forEach(operationParserRegistry.getAnnotationParsers(), new Predicate<OperationMethodAnnotationDefinitionParser<? extends Annotation>>() {
                @Override
                public boolean test(OperationMethodAnnotationDefinitionParser<? extends Annotation> parser) {
                    return Reflects.getAnnotation(method, parser.getAnnotation()) != null;
                }
            }, new Consumer<OperationMethodAnnotationDefinitionParser<? extends Annotation>>() {
                @Override
                public void accept(OperationMethodAnnotationDefinitionParser<? extends Annotation> parser) {
                    operationDefinition.set(parser.parse(method));
                }
            }, new Predicate<OperationMethodAnnotationDefinitionParser<? extends Annotation>>() {
                @Override
                public boolean test(OperationMethodAnnotationDefinitionParser<? extends Annotation> parser) {
                    return !operationDefinition.isNull();
                }
            });

            // 2.2) parse from configuration file
            if (operationDefinition.isNull()) {
                // 2.2.1 using custom operation id generator
                Collects.forEach(operationIdGenerators, new Consumer<OperationIdGenerator<AuditedRequest, MethodInvocation>>() {
                    @Override
                    public void accept(OperationIdGenerator<AuditedRequest, MethodInvocation> generator) {
                        String operationDefinitionId = generator.get(wrappedRequest);
                        operationDefinition.set((OperationDefinition) operationDefinitionRepository.getById(operationDefinitionId));
                    }
                }, new Predicate<OperationIdGenerator<AuditedRequest, MethodInvocation>>() {
                    @Override
                    public boolean test(OperationIdGenerator<AuditedRequest, MethodInvocation> value) {
                        return !operationDefinition.isNull();
                    }
                });
            }

            if (operationDefinition.isNull()) {
                // 2.2.2 using method full name (exclude parameters)
                String operationDefinitionId = Reflects.getFQNClassName(method.getDeclaringClass()) + "." + method.getName();
                operationDefinition.set((OperationDefinition) operationDefinitionRepository.getById(operationDefinitionId));
            }
            if (!operationDefinition.isNull()) {
                methodOperationDefinitionCache.set(method, operationDefinition.get().getId());
            }
        }
        return operationDefinition.get();
    }


    public MultipleLevelConfigurationRepository getOperationDefinitionRepository() {
        return operationDefinitionRepository;
    }

    public void setOperationDefinitionRepository(MultipleLevelConfigurationRepository operationDefinitionRepository) {
        this.operationDefinitionRepository = operationDefinitionRepository;
    }

    public Cache<Method, String> getMethodOperationDefinitionCache() {
        return methodOperationDefinitionCache;
    }

    public void setMethodOperationDefinitionCache(Cache<Method, String> methodOperationDefinitionCache) {
        this.methodOperationDefinitionCache = methodOperationDefinitionCache;
    }

    public OperationDefinitionParserRegistry getOperationParserRegistry() {
        return operationParserRegistry;
    }

    public void setOperationParserRegistry(OperationDefinitionParserRegistry operationParserRegistry) {
        this.operationParserRegistry = operationParserRegistry;
    }

    public List<OperationIdGenerator<AuditedRequest, MethodInvocation>> getOperationIdGenerators() {
        return operationIdGenerators;
    }

    public void setOperationIdGenerators(List<OperationIdGenerator<AuditedRequest, MethodInvocation>> operationIdGenerators) {
        this.operationIdGenerators = operationIdGenerators;
    }

    public void addOperationIdGenerator(OperationIdGenerator<AuditedRequest, MethodInvocation> generator) {
        this.operationIdGenerators.add(generator);
    }


    public OperationParametersExtractor<AuditedRequest, MethodInvocation> getOperationParametersExtractor() {
        return operationParametersExtractor;
    }

    public void setOperationParametersExtractor(OperationParametersExtractor<AuditedRequest, MethodInvocation> operationParametersExtractor) {
        this.operationParametersExtractor = operationParametersExtractor;
    }


}
