package com.jn.audit.core.annotation;

public @interface Operation {
    String code() default "";    //  class fullname +"."+methodName

    String name() default "";    //

    String description() default ""; //

    String type() default ""; //
}
