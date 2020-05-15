package com.jn.audit.core.resource.parser.clazz;

import com.jn.audit.core.annotation.ResourceId;
import com.jn.audit.core.annotation.ResourceMapping;
import com.jn.audit.core.annotation.ResourceName;
import com.jn.audit.core.annotation.ResourceType;
import com.jn.audit.core.model.Resource;
import com.jn.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.valuegetter.MemberValueGetter;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Modifiers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 只创建一个实例即可
 *
 * @param <T> the
 * @see DefaultEntityClassResourceSupplierParser
 * @deprecated
 */
@Singleton
@Deprecated
public class AnnotatedEntityResourceSupplierParser<T> implements EntityClassResourceSupplierParser<T> {
    private static final Logger logger = LoggerFactory.getLogger(AnnotatedEntityResourceSupplierParser.class);
    public static final AnnotatedEntityResourceSupplierParser DEFAULT_INSTANCE = new AnnotatedEntityResourceSupplierParser();

    @Override
    public EntityResourceSupplier<T> parse(Class<T> entityClass) {

        Map<String, MemberValueGetter> getterMap = parseByResourceMappingAnnotation(entityClass);
        if (Emptys.isEmpty(getterMap)) {
            Map<String, MemberValueGetter> getterMap2 = parseByResourceFieldAnnotation(entityClass);
            getterMap.putAll(getterMap2);
        }
        if (Emptys.isEmpty(getterMap)) {
            return null;
        }
        EntityResourceSupplier supplier = new EntityResourceSupplier(entityClass);
        supplier.register(getterMap);
        return supplier;
    }

    /**
     * 基于 {@link com.jn.audit.core.annotation.ResourceMapping} 进行解析
     *
     * @return
     */
    private Map<String, MemberValueGetter> parseByResourceMappingAnnotation(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = new HashMap<String, MemberValueGetter>();
        ResourceMapping resourceMapping = Reflects.getAnnotation(entityClass, ResourceMapping.class);
        if (resourceMapping != null) {
            String idField = resourceMapping.id();
            parsePropertyByFieldName(entityClass, idField, map, Resource.RESOURCE_ID);

            String nameField = resourceMapping.name();
            parsePropertyByFieldName(entityClass, nameField, map, Resource.RESOURCE_NAME);

            String typeField = resourceMapping.type();
            parsePropertyByFieldName(entityClass, typeField, map, Resource.RESOURCE_TYPE);
        }
        return map;
    }

    private Map<String, MemberValueGetter> parseByResourceFieldAnnotation(Class<T> entityClass) {
        Map<String, MemberValueGetter> map = new HashMap<String, MemberValueGetter>();

        parsePropertyByAnnotation(entityClass, ResourceId.class, map, Resource.RESOURCE_ID);

        parsePropertyByAnnotation(entityClass, ResourceName.class, map, Resource.RESOURCE_NAME);

        parsePropertyByAnnotation(entityClass, ResourceType.class, map, Resource.RESOURCE_TYPE);

        return map;
    }


    private void parsePropertyByAnnotation(Class<T> entityClass, final Class<? extends Annotation> annotationClass, Map<String, MemberValueGetter> map, String resourceProperty) {
        Field field = Pipeline.of(entityClass.getDeclaredFields()).findFirst(new Predicate<Field>() {
            @Override
            public boolean test(Field f) {
                return !Modifiers.isStatic(f) && Reflects.hasAnnotation(f, annotationClass);
            }
        });

        if (field != null) {
            map.put(resourceProperty, new MemberValueGetter(field));
        } else {
            logger.info("Can't find the resource {} property in the class: {} when parse it using {} ", resourceProperty, entityClass, annotationClass);
        }
    }

    /**
     * 从entityClass 类中解析 resourceProperty ，解析时使用的字段名称是 fieldName，解析完之后放入map
     */
    private void parsePropertyByFieldName(Class<T> entityClass, String fieldName, Map<String, MemberValueGetter> map, String resourceProperty) {
        Field field = Reflects.getAnyField(entityClass, fieldName);
        if (field != null) {
            map.put(resourceProperty, new MemberValueGetter(field));
        } else {
            logger.info("Can't find the resource {} property in the class: {} when parse it using @ResourceMapping ", resourceProperty, entityClass);
        }
    }


}
