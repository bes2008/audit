package com.jn.audit.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 用在一个Entity类上，用于标注 分别代表 id, name的字段名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, PARAMETER})
public @interface ResourceMapping {
    /**
     * @see com.jn.audit.core.model.Resource resourceName
     */
    String name();

    /**
     * @see com.jn.audit.core.model.Resource resourceId
     */
    String id();

    /**
     * @see com.jn.audit.core.model.Resource resourceType
     */
    String type() default "";
}
