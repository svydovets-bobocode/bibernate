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
 * of an entity or mapped superclass in conjunction with the {@link Id} annotation. The <code>
 * GeneratedValue</code> annotation supports MANUAL, IDENTITY and SEQUENCE id generation strategies
 *
 * <pre>
 * Examples:
 *
 *     &#064Id
 *     &#064GenerationValue(GenerationType.MANUAL)
 *     Long id;
 *
 *     &#064Id
 *     &#064GenerationValue(GenerationType.IDENTITY)
 *     Long id;
 *
 *     &#064Id
 *     &#064GenerationValue(GenerationType.SEQUENCE,
 *                          sequenceName="custom_seq",
 *                          allocationSize=50)
 *     Long id;
 *
 * </pre>
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
     * (Optional) Could be provided along with SEQUENCE GenerationType only for specifying the
     * sequence name; Otherwise the default value will be build according to database convention
     */
    String sequenceName() default "";

    /**
     * (Optional) Specify the "cash" size for id values. Won't select sequence till the cash range
     * isn't completely used. <br>
     * Could be provided along with SEQUENCE GenerationType only for specifying the allocation size.
     * Should be aligned with sequence table increment value in DB.
     */
    int allocationSize() default 1;
}
