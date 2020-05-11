package com.jn.audit.core.resource.parser;

import com.jn.audit.core.annotation.ResourceId;
import com.jn.audit.core.annotation.ResourceName;
import com.jn.audit.core.annotation.ResourceType;
import com.jn.audit.core.model.Resource;
import com.jn.audit.core.resource.supplier.IterableResourceSupplier;
import com.jn.audit.core.resource.valuegetter.IterableValueGetter;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 如果一个方法的多个参数中，具有 @ResourceId, @ResourceName, @ResourceType 中的任意一个，都可以用它来解析
 *
 * @see ResourceId
 * @see ResourceName
 * @see ResourceType
 *
 * @see CustomResourcePropertyParameterResourceSupplierParser
 */
public class ResourcePropertyAnnotatedResourceSupplierParser implements ResourceSupplierParser<Parameter[], IterableResourceSupplier> {
    @Override
    public IterableResourceSupplier parse(Parameter[] parameters) {

        // key: resourceProperty
        Map<String, IterableValueGetter> getterMap = new HashMap<>();
        Pipeline.of(parameters).forEach(new Consumer2<Integer, Parameter>() {
            @Override
            public void accept(Integer index, Parameter parameter) {
                if (Reflects.hasAnnotation(parameter, ResourceId.class)) {
                    getterMap.put(Resource.RESOURCE_ID, new IterableValueGetter(index));
                } else if (Reflects.hasAnnotation(parameter, ResourceName.class)) {
                    getterMap.put(Resource.RESOURCE_NAME, new IterableValueGetter(index));
                } else if (Reflects.hasAnnotation(parameter, ResourceType.class)) {
                    getterMap.put(Resource.RESOURCE_TYPE, new IterableValueGetter(index));
                }
            }
        });
        if (Emptys.isEmpty(getterMap)) {
            return null;
        }

        IterableResourceSupplier supplier = new IterableResourceSupplier();
        supplier.register(getterMap);
        return supplier;
    }
}
