package com.jn.audit.entityloader.resttemplate;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.MapAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

public class DefaultParameterizedResponseClassProvider implements ParameterizedResponseClassProvider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultParameterizedResponseClassProvider.class);

    @Override
    public ParameterizedTypeReference get(String url, HttpMethod httpMethod, ResourceDefinition resourceDefinition) {
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        String entityClassName = mapAccessor.getString("entityClass");
        Preconditions.checkNotNull(entityClassName);
        ClassLoader cl = DefaultParameterizedResponseClassProvider.class.getClassLoader();
        if (ClassLoaders.hasClass(entityClassName, cl)) {
            try {
                Class clazz = ClassLoaders.loadClass(entityClassName, cl);
                return ParameterizedTypeReference.forType(clazz);
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Can't find the entity class: {}", entityClassName));
    }
}
