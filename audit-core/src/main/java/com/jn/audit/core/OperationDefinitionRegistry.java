package com.jn.audit.core;

import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.model.OperationImportance;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.Preconditions;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class OperationDefinitionRegistry {
    private Map<String, OperationImportance> importanceMap = new HashMap<String, OperationImportance>();
    private Map<String, OperationDefinition> definitionMap = new HashMap<String, OperationDefinition>();


    private static final OperationDefinitionRegistry instance = new OperationDefinitionRegistry();

    private OperationDefinitionRegistry() {
    }

    public static OperationDefinitionRegistry getInstance() {
        return instance;
    }

    public void addImportance(OperationImportance importance){
        importanceMap.put(importance.getName(), importance);
    }

    public void addDefinition(OperationDefinition operationDefinition) {
        Preconditions.checkNotNull(operationDefinition);
        definitionMap.put(operationDefinition.getCode(), operationDefinition);
        OperationImportance importance = operationDefinition.getImportance();
        if (importance != null) {
            addImportance(importance);
        }
    }

    public OperationImportance getImportance(String key){
        return importanceMap.get(key);
    }

    public OperationDefinition getDefinition(String code){
        return definitionMap.get(code);
    }
}
