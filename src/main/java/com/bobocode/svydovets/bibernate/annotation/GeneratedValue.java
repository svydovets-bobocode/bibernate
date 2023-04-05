package com.bobocode.svydovets.bibernate.annotation;

import static java.lang.annotation.ElementType.FIELD;

import com.bobocode.svydovets.bibernate.constant.GenerationType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratedValue {
    GenerationType strategy() default GenerationType.MANUAL;

    String sequenceName() default "";

    int allocationSize() default 50;
}
