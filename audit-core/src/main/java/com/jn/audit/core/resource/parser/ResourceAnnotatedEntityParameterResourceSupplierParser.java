package com.jn.audit.core.resource.parser;

import com.jn.audit.core.annotation.Resource;
import com.jn.audit.core.annotation.ResourceMapping;
import com.jn.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Parameter;

/**
 * 如果在一个方法的某个参数，是一个Entity时，并且具有 @Resource 注解时，将解析成实体的资源
 *
 * @see Resource
 * @see CustomNamedEntityResourceSupplierParser
 */
public class ResourceAnnotatedEntityParameterResourceSupplierParser<T> implements ResourceSupplierParser<Parameter, EntityResourceSupplier<T>> {

    @Override
    public EntityResourceSupplier<T> parse(Parameter parameter) {
        if (!Reflects.hasAnnotation(parameter, Resource.class)) {
            return null;
        }
        Resource resource = Reflects.getAnnotation(parameter, Resource.class);
        ResourceMapping[] mappings = resource.value();
        if (Emptys.isEmpty(mappings)) {
            return DefaultEntityClassResourceSupplierParser.DEFAULT_INSTANCE.parse(parameter.getType());
        } else {
            ResourceMapping mapping = Pipeline.of(mappings).findFirst(new Predicate<ResourceMapping>() {
                @Override
                public boolean test(ResourceMapping mapping) {
                    return Emptys.isNotEmpty(mapping.id()) && Emptys.isNotEmpty(mapping.name());
                }
            });
            StringMap map = new StringMap();
            map.put(com.jn.audit.core.model.Resource.RESOURCE_ID, mapping.id());
            map.put(com.jn.audit.core.model.Resource.RESOURCE_NAME, mapping.name());
            map.put(com.jn.audit.core.model.Resource.RESOURCE_TYPE, mapping.type());
            CustomNamedEntityResourceSupplierParser parser = new CustomNamedEntityResourceSupplierParser(map);
            return parser.parse(parameter.getType());
        }
    }


}
