package com.bobocode.svydovets.bibernate.annotation;

import static java.lang.annotation.ElementType.FIELD;

import com.bobocode.svydovets.bibernate.constant.GenerationType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides for the specification of generation strategies for the values of primary keys.
 *
 * <p>The <code>GeneratedValue</code> annotation may be applied to a primary key property or field
 * of an entity annotated with {@link Id}.
 */
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratedValue {
    /**
     * The primary key generation strategy that the persistence provider must use to generate the
     * annotated entity primary key.
     */
    GenerationType strategy() default GenerationType.MANUAL;

    /**
     * Used to specify the name of the database sequence object that should be used to generate
     * primary key values for the entity.
     */
    String sequenceName() default "";

    /**
     * Indicates how many primary key values should be allocated at once from the database sequence.
     */
    int allocationSize() default 50;
}
