package com.jn.audit.core.resource.parser.clazz;

import com.jn.audit.core.model.Resource;
import com.jn.audit.core.model.ResourceDefinition;
import com.jn.audit.core.resource.parser.parameter.ResourceAnnotatedEntityParameterResourceSupplierParser;
import com.jn.langx.util.valuegetter.MemberValueGetter;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.collection.StringMapAccessor;

import java.util.HashMap;
import java.util.Map;


/**
 * 需要每一次解析创建一个
 *
 * 针对 实体类的解析
 *
 * @see ResourceDefinition#getResource()
 *
 * @see ResourceAnnotatedEntityParameterResourceSupplierParser
 *
 * @param <T>
 */
public class CustomNamedEntityResourceSupplierParser<T> extends FieldNameEntityResourceSupplierParser<T> {

    /**
     * resourceXxx to custom file name
     */
    private StringMap nameMapping;

    public CustomNamedEntityResourceSupplierParser(StringMap map) {
        this.nameMapping = map;
    }

    public CustomNamedEntityResourceSupplierParser(Map<String, String> map) {
        this.nameMapping = new StringMap(map);
    }

    protected Map<String, MemberValueGetter> parseByFieldName(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = new HashMap<String, MemberValueGetter>();

        StringMapAccessor accessor = new StringMapAccessor(nameMapping);
        parsePropertyByFieldName(entityClass, accessor.getString(Resource.RESOURCE_ID, Resource.RESOURCE_ID), map, Resource.RESOURCE_ID);

        parsePropertyByFieldName(entityClass, accessor.getString(Resource.RESOURCE_NAME, Resource.RESOURCE_NAME), map, Resource.RESOURCE_NAME);

        parsePropertyByFieldName(entityClass, accessor.getString(Resource.RESOURCE_TYPE, Resource.RESOURCE_TYPE), map, Resource.RESOURCE_TYPE);
        return map;
    }
}
