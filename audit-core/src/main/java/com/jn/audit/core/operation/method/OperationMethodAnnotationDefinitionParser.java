package com.jn.audit.core.operation.method;

import com.jn.audit.core.operation.OperationDefinitionParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface OperationMethodAnnotationDefinitionParser<E extends Annotation> extends OperationDefinitionParser<Method> {
    Class<E> getAnnotation();
}
