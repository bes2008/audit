package com.jn.audit.core.resource.parser;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.supplier.IterableResourceSupplier;
import com.jn.langx.util.valuegetter.IterableValueGetter;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @see ResourceDefinition#getResourceId()
 * @see ResourceDefinition#getResourceName()
 * @see ResourceDefinition#getResourceType()
 * @see ResourcePropertyAnnotatedResourceSupplierParser
 */
public class CustomResourcePropertyParameterResourceSupplierParser implements ResourceSupplierParser<Parameter[], IterableResourceSupplier> {
    /**
     * resource property to parameter name mapping
     */
    private Map<String, String> parameterResourceMapping = new HashMap<String, String>();

    public CustomResourcePropertyParameterResourceSupplierParser(Map<String, Object> mapping) {
        Collects.forEach(mapping, new Consumer2<String, Object>() {
            @Override
            public void accept(String key, Object value) {
                parameterResourceMapping.put(key, value.toString());
            }
        });
    }

    @Override
    public IterableResourceSupplier parse(final Parameter[] parameters) {
        Map<String, IterableValueGetter> getterMap = new HashMap<>();
        Pipeline.of(parameters).forEach(new Consumer2<Integer, Parameter>() {
            @Override
            public void accept(final Integer index, Parameter parameter) {
                String parameterName = parameter.getName();
                Collects.forEach(parameterResourceMapping, new Consumer2<String, String>() {
                    @Override
                    public void accept(String resourceProperty, String parameterName0) {
                        if (parameterName.equals(parameterName0)) {
                            getterMap.put(resourceProperty, new IterableValueGetter(index));
                        }
                    }
                });
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
