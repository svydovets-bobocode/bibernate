package com.bobocode.svydovets.bibernate.annotation;

import com.bobocode.svydovets.bibernate.session.Session;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for Many - to - One DB relation (child entity has one parent entity, when parent entity can
 * have multiple children). All the mappings should be properly configured on the child side.
 *
 * <p>In order to for the mapping to work correctly in should be used among with the {@link
 * JoinColumn} annotation. There you should provide a name for the foreign key column name, where
 * the child entity keeps the reference to the parent entity.
 *
 * <p>When you retrieve from the DB the child entity, the parent entity will be eagerly loaded and
 * set too. There is no need to perform the explicit loading of the parent entity. Currently, you
 * cannot configure it if you want to load the parent lazily.
 *
 * <p>Currently, by default, no cascade operations are performed with the parent entity.
 *
 * @see Session#find(Class, Object)
 * @see JoinColumn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {}
