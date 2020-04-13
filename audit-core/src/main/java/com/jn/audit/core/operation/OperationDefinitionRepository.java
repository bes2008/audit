package com.jn.audit.core.operation;

import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.model.OperationImportance;
import com.jn.langx.configuration.AbstractConfigurationRepository;
import com.jn.langx.util.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class OperationDefinitionRepository extends AbstractConfigurationRepository {
    private Map<String, OperationImportance> importanceMap = new HashMap<String, OperationImportance>();
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
        definitionMap.put(operationDefinition.getCode(), operationDefinition);
        OperationImportance importance = operationDefinition.getImportance();
        if (importance != null) {
            registerImportance(importance);
        }
    }

    public OperationImportance getImportance(String key) {
        return importanceMap.get(key);
    }

    public OperationDefinition getDefinition(String code) {
        return definitionMap.get(code);
    }
}
