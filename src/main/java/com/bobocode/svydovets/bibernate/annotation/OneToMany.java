package com.bobocode.svydovets.bibernate.annotation;

import com.bobocode.svydovets.bibernate.lazy.SvydovetsLazyList;
import com.bobocode.svydovets.bibernate.session.Session;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Used for One-to-Many DB relation (when parent entity have multiple relations with the child
 * entities, child entity stores the reference to the parent via the foreign key column).
 *
 * <p>Should be applied to the Entity collection field, for example:
 *
 * <pre>{@code
 * CREATE TABLE parent (
 *      id INT PRIMARY KEY,
 *      name TEXT NOT NULL
 * );
 * <p>
 * CREATE TABLE child (
 *      id INT PRIMARY KEY,
 *      name TEXT NOT NULL,
 *      parent_id INT REFERENCES parent(id)
 * );
 * }</pre>
 *
 * <p>The corresponding entities mapping would be the next:
 *
 * <pre>{@code
 * @Entity
 * @Table(value = "parent")
 * public class Parent {
 *     @Id private Long id;
 *
 *     @Column private String name;
 *
 *     @OneToMany private List<Child> children = new ArrayList<>();
 * }
 *
 * @Entity
 * @Table(value = "child")
 * public class Child {
 *     @Id private Long id;
 *
 *     @Column private String name;
 * }
 * }</pre>
 *
 * <p>This type of the relation does not immediately load from the DB. Also, your provided
 * collection will not be used at all. Instead, the Bibernate ORM will create the instance of the
 * {@link SvydovetsLazyList}. It is the implementation of the {@link java.util.List} interface, but
 * it acts as a lazy collection. The entities will be loaded only at the first time that you access
 * it.
 *
 * @see Session#find(Class, Object)
 * @see Session#findAllBy(Class, Field, Object)
 * @see SvydovetsLazyList
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {}
