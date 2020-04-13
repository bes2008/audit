package com.jn.audit.core.operation;

import com.jn.audit.core.model.OperationDefinition;
import com.jn.langx.configuration.FullLoadConfigurationLoader;

import java.util.List;

public class YamlOperationDefinitionLoader implements FullLoadConfigurationLoader<OperationDefinition> {
    @Override
    public List<OperationDefinition> loadAll() {
        return null;
    }

    @Override
    public OperationDefinition load(String id) {
        return null;
    }
}
