package com.jn.audit.core.resource.parser;

import com.jn.audit.core.annotation.Resource;
import com.jn.audit.core.annotation.ResourceMapping;
import com.jn.audit.core.resource.supplier.MapResourceSupplier;
import com.jn.langx.util.valuegetter.MapValueGetter;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 一个方法的参数是 Map，且有 @Resource 注解时，可以用它来处理
 *
 * @see Resource
 * @see CustomNamedMapParameterResourceSupplierParser
 */
public class ResourceAnnotatedMapParameterResourceSupplierParser implements ResourceSupplierParser<Parameter, MapResourceSupplier> {
    @Override
    public MapResourceSupplier parse(Parameter parameter) {
        if (!Reflects.isSubClassOrEquals(Map.class, parameter.getType())) {
            return null;
        }
        if (!Reflects.hasAnnotation(parameter, Resource.class)) {
            return null;
        }
        Resource resource = Reflects.getAnnotation(parameter, Resource.class);
        ResourceMapping[] mappings = resource.value();
        if (Emptys.isNotEmpty(mappings)) {
            ResourceMapping mapping = Pipeline.of(mappings).findFirst(new Predicate<ResourceMapping>() {
                @Override
                public boolean test(ResourceMapping mapping) {
                    return Emptys.isNotEmpty(mapping.name());
                }
            });
            if (mapping == null) {
                return null;
            }
            MapResourceSupplier supplier = new MapResourceSupplier();
            if (Emptys.isNotEmpty(mapping.id())) {
                supplier.register(com.jn.audit.core.model.Resource.RESOURCE_ID, new MapValueGetter(mapping.id()));
            }
            supplier.register(com.jn.audit.core.model.Resource.RESOURCE_NAME, new MapValueGetter(mapping.name()));
            if (Emptys.isNotEmpty(mapping.type())) {
                supplier.register(com.jn.audit.core.model.Resource.RESOURCE_TYPE, new MapValueGetter(mapping.type()));
            }

            return supplier;
        }
        return null;
    }

}
