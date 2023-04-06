package com.bobocode.svydovets.bibernate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the primary table for the annotated entity.
 *
 * <p> If no <code>Table</code> annotation is specified for an entity
 * class, the default values apply in lower case.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * (Optional) The name of the table.
     * <p> Defaults to the entity name.
     */
    String value();
}
