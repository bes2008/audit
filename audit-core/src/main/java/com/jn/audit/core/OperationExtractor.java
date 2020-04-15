package com.jn.audit.core;

import com.jn.audit.core.annotation.Operation;
import com.jn.langx.util.function.Supplier;

public interface OperationExtractor<Request> extends Supplier<Request, Operation> {

}
