package com.jn.audit.swagger.operation;

import com.jn.audit.core.annotation.Resource;
import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.operation.method.OperationMethodAnnotationDefinitionParser;
import com.jn.audit.core.operation.method.ResourceDefinitionParser;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.reflect.Reflects;
import io.swagger.annotations.ApiOperation;

import java.lang.reflect.Method;

public class SwaggerApiOperationAnnotationParser implements OperationMethodAnnotationDefinitionParser<ApiOperation> {

    private ResourceDefinitionParser resourceDefinitionParser = new ResourceDefinitionParser();

    @Override
    public Class<ApiOperation> getAnnotation() {
        return ApiOperation.class;
    }

    @Override
    public String getName() {
        return "Swagger-ApiOperation-Parser";
    }

    @Override
    public OperationDefinition parse(Method method) {
        ApiOperation operation = Reflects.getAnnotation(method, getAnnotation());

        if (operation != null) {
            OperationDefinition operationDefinition = new OperationDefinition();
            operationDefinition.setId(operation.nickname());
            String id = operation.nickname();
            if (Emptys.isEmpty(id)) {
                id = Reflects.getFQNClassName(method.getDeclaringClass()) + method.getName();
            }
            String code = id;
            String name = operation.value();
            operationDefinition.setId(id);
            operationDefinition.setCode(code);
            operationDefinition.setName(name);

            Resource resource = Reflects.getAnnotation(method, Resource.class);
            ResourceDefinition resourceDefinition = null;
            if (resource != null) {
                resourceDefinition = resourceDefinitionParser.parse(resource);
            } else {
                resourceDefinition = new ResourceDefinition();
            }

            operationDefinition.setResourceDefinition(resourceDefinition);
        }

        return null;
    }
}
