package com.jn.audit.core.operation.repository;

import com.jn.audit.core.model.OperationDefinition;

public class NoopOperationRepositoryParser implements OperationRepositoryParser{
    @Override
    public String getName() {
        return "Audit-NOOP-Operation-Repository-parser";
    }

    @Override
    public String getRepositoryName() {
        return "Audit-NOOP-Operation-Repository";
    }

    @Override
    public OperationDefinition parse(String input) {
        return null;
    }
}
