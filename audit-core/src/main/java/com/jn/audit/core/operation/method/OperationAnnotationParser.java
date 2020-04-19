package com.jn.audit.core.operation.method;

import com.jn.audit.core.annotation.Operation;
import com.jn.audit.core.model.OperationDefinition;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Method;

public class OperationAnnotationParser implements OperationMethodAnnotationDefinitionParser<Operation> {
    @Override
    public Class<Operation> getAnnotation() {
        return Operation.class;
    }


    @Override
    public OperationDefinition parse(Method method) {
        Operation operation = Reflects.getAnnotation(method, getAnnotation());
        if (operation != null) {
            OperationDefinition operationDefinition = new OperationDefinition();
            operationDefinition.setId(operation.code());
            operationDefinition.setCode(operation.code());
            operationDefinition.setName(operation.name());
            operationDefinition.setType(operation.type());
            operationDefinition.setDescription(operation.description());
            return operationDefinition;
        }
        return null;
    }
}
