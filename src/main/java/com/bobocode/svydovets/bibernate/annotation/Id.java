package com.bobocode.svydovets.bibernate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.UUID;

/**
 * Specifies the primary key of an entity. The field or property to which the Id annotation is
 * applied should be one of the types are listed in {@link Id#SUPPORTED_OBJECT_TYPES}. The mapped
 * column for the primary key of the entity is assumed to be the primary key of table in the
 * database.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
    Set<Class<?>> SUPPORTED_OBJECT_TYPES =
            Set.of(
                    Integer.class,
                    Long.class,
                    UUID.class,
                    String.class,
                    BigDecimal.class,
                    BigInteger.class,
                    java.util.Date.class,
                    java.sql.Date.class,
                    int.class,
                    long.class);
}
