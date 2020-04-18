package com.jn.audit.core.operation;

import com.jn.audit.core.AuditRequest;
import com.jn.audit.core.model.Operation;
import com.jn.langx.util.function.Supplier;

import java.util.List;
import java.util.Map;

public interface OperationParametersExtractor<AuditedRequest, AuditedRequestContext> extends Supplier<AuditRequest<AuditedRequest, AuditedRequestContext>, Map<String, List<String>>> {


}
