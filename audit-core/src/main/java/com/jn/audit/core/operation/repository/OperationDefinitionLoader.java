package com.jn.audit.core.operation.repository;

import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.model.OperationImportance;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.configuration.FullLoadConfigurationLoader;

import java.util.List;

public interface OperationDefinitionLoader extends FullLoadConfigurationLoader<OperationDefinition> {
    @NonNull
    List<OperationDefinition> reload(@Nullable List<OperationImportance> importances);

    void setDefinitionFilePath(@NonNull String definitionFilePath);
}
