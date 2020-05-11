package com.jn.audit.core.resource.parser;

import com.jn.audit.core.resource.supplier.MapResourceSupplier;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.Parameter;
import java.util.Map;

public class CustomNamedMapParameterResourceSupplierParser implements ResourceSupplierParser<Parameter, MapResourceSupplier> {

    /**
     * resourceXxx to custom file name
     */
    private StringMap nameMapping;

    public CustomNamedMapParameterResourceSupplierParser(StringMap map) {
        this.nameMapping = map;
    }

    public CustomNamedMapParameterResourceSupplierParser(Map<String, String> map) {
        this.nameMapping = new StringMap(map);
    }

    @Override
    public MapResourceSupplier parse(Parameter parameter) {
        if (!Reflects.isSubClassOrEquals(Map.class, parameter.getType())) {
            return null;
        }
        return null;
    }
}
