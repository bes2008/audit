package com.jn.audit.core.resource.parser;

import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.supplier.IterableResourceSupplier;
import com.jn.audit.core.resource.valuegetter.IterableIndexValueGetter;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.function.Consumer2;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @see ResourceDefinition#getResourceId()
 * @see ResourceDefinition#getResourceName()
 * @see ResourceDefinition#getResourceType()
 *
 * @see ResourcePropertyAnnotatedResourceSupplierParser
 */
public class CustomResourcePropertyParameterResourceSupplierParser implements ResourceSupplierParser<Parameter[], IterableResourceSupplier> {
    /**
     * resource property to parameter name mapping
     */
    private StringMap parameterResourceMapping;

    public CustomResourcePropertyParameterResourceSupplierParser(StringMap mapping) {
        this.parameterResourceMapping = mapping;
    }

    public CustomResourcePropertyParameterResourceSupplierParser(Map<String, String> mapping) {
        this(new StringMap(mapping));
    }

    @Override
    public IterableResourceSupplier parse(final Parameter[] parameters) {
        Map<String, IterableIndexValueGetter> getterMap = new HashMap<>();
        Pipeline.of(parameters).forEach(new Consumer2<Integer, Parameter>() {
            @Override
            public void accept(final Integer index, Parameter parameter) {
                String parameterName = parameter.getName();
                Collects.forEach(parameterResourceMapping, new Consumer2<String, String>() {
                    @Override
                    public void accept(String resourceProperty, String parameterName0) {
                        if (parameterName.equals(parameterName0)) {
                            getterMap.put(resourceProperty, new IterableIndexValueGetter(index));
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
