package com.jn.audit.core.resource.parser.clazz;

import com.jn.audit.core.model.Resource;
import com.jn.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.valuegetter.MemberValueGetter;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 只创建一个实例即可
 * @param <T> the entity class
 * @deprecated
 * @see DefaultEntityClassResourceSupplierParser
 */
@Singleton
@Deprecated
public class FieldNameEntityResourceSupplierParser<T> implements EntityClassResourceSupplierParser<T> {
    private static final Logger logger = LoggerFactory.getLogger(FieldNameEntityResourceSupplierParser.class);
    public static final FieldNameEntityResourceSupplierParser DEFAULT_INSTANCE = new FieldNameEntityResourceSupplierParser();
    @Override
    public EntityResourceSupplier<T> parse(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = parseByFieldName(entityClass);
        if (Emptys.isEmpty(map)) {
            return null;
        }

        EntityResourceSupplier<T> supplier = new EntityResourceSupplier<>(entityClass);
        supplier.register(map);

        return supplier;
    }

    protected Map<String, MemberValueGetter> parseByFieldName(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = new HashMap<String, MemberValueGetter>();

        parsePropertyByFieldName(entityClass, Resource.RESOURCE_ID, map, Resource.RESOURCE_ID);

        parsePropertyByFieldName(entityClass, Resource.RESOURCE_NAME, map, Resource.RESOURCE_NAME);

        parsePropertyByFieldName(entityClass, Resource.RESOURCE_TYPE, map, Resource.RESOURCE_TYPE);
        return map;
    }

    /**
     * 从entityClass 类中解析 resourceProperty ，解析时使用的字段名称是 fieldName，解析完之后放入map
     */
    protected void parsePropertyByFieldName(Class<T> entityClass, String fieldName, Map<String, MemberValueGetter> map, String resourceProperty) {
        Field field = Reflects.getAnyField(entityClass, fieldName);
        if (field != null) {
            map.put(resourceProperty, new MemberValueGetter(field));
        } else {
            logger.info("Can't find the resource {} property in the class: {} when parse it using file name ", resourceProperty, entityClass);
        }
    }
}
