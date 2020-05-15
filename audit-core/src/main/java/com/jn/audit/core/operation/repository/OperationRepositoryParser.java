package com.jn.audit.core.operation.repository;

import com.jn.audit.core.operation.OperationDefinitionParser;

public interface OperationRepositoryParser extends OperationDefinitionParser<String> {
    String getName();

    String getRepositoryName();
}
