package com.bobocode.svydovets.bibernate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

/**
 * Specifies the version field or property of an entity class that serves as its optimistic lock
 * value. Only a single Version property or field should be used per class. The field to which the
 * Version annotation is applied should be one of the types are listed in {@link
 * Version#SUPPORTED_OBJECT_TYPES}. All changes you made manually in the code to the field annotated
 * with Version annotation will be ignored. Version value incrementing happens automatically while
 * applying changes to the database.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Version {
    Set<Class<?>> SUPPORTED_OBJECT_TYPES =
            Set.of(Short.class, Integer.class, Long.class, short.class, int.class, long.class);
}
