package com.jn.audit.core.operation;

import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.model.OperationImportance;
import com.jn.langx.configuration.AbstractConfigurationRepository;
import com.jn.langx.configuration.ConfigurationWriter;
import com.jn.langx.configuration.FullLoadConfigurationLoader;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationDefinitionRepository extends AbstractConfigurationRepository<OperationDefinition, FullLoadConfigurationLoader<OperationDefinition>, ConfigurationWriter<OperationDefinition>> {
    /**
     * key: OperationImportance#getKey()
     */
    private Map<String, OperationImportance> importanceMap = new HashMap<String, OperationImportance>();
    /**
     * key: code
     * value: definition
     */
    private Map<String, OperationDefinition> definitionMap = new HashMap<String, OperationDefinition>();


    private static final OperationDefinitionRepository instance = new OperationDefinitionRepository();

    private OperationDefinitionRepository() {
    }

    public static OperationDefinitionRepository getInstance() {
        return instance;
    }

    public void registerImportance(OperationImportance importance) {
        importanceMap.put(importance.getName(), importance);
    }

    public void registerDefinition(OperationDefinition operationDefinition) {
        Preconditions.checkNotNull(operationDefinition);
        this.add(operationDefinition);
        definitionMap.put(operationDefinition.getCode(), operationDefinition);
        OperationImportance importance = operationDefinition.getImportance();
        if (importance != null) {
            registerImportance(importance);
        }
    }

    public OperationImportance getImportance(String key) {
        return importanceMap.get(key);
    }

    public OperationDefinition getDefinitionByCode(String code) {
        return definitionMap.get(code);
    }

    @Override
    public void reload() {
        super.reload();
        List<OperationDefinition> definitions = loader.loadAll();
        Collects.forEach(definitions, new Consumer<OperationDefinition>() {
            @Override
            public void accept(OperationDefinition operationDefinition) {
                registerDefinition(operationDefinition);
            }
        });
    }
}
