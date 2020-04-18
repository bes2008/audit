package com.jn.audit.core.operation;

import com.jn.audit.core.model.Operation;
import com.jn.langx.Parser;

import java.lang.annotation.Annotation;

public interface OperationAnnotationParser<E extends Annotation> extends Parser<E, Operation> {

}
