package com.jn.audit.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * 用在一个Entity类上，用于标注 分别代表 id, name的字段名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface ResourceMapping {
    String name();

    String id();

    String type() default "";
}
