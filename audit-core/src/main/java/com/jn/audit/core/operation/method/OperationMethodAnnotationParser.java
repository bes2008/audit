package com.jn.audit.core.operation.method;

import com.jn.audit.core.operation.OperationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface OperationMethodAnnotationParser<E extends Annotation> extends OperationParser<Method> {
    Class<E> getAnnotation();
}
