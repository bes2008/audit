package com.jn.audit.core.operation.repository;

import com.jn.audit.core.operation.OperationParser;

public interface OperationRepositoryParser extends OperationParser<String> {
    String getName();
    String getRepositoryName();
}
