package com.bobocode.svydovets.bibernate.annotation;

import com.bobocode.svydovets.bibernate.session.Session;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used among with the {@link ManyToOne} mapping in order to provide the ORM with the information
 * about the foreign key relation column name. See the {@link JoinColumn#name()} for more details
 * and an example.
 *
 * @see ManyToOne
 * @see JoinColumn#name()
 * @see Session#find(Class, Object)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JoinColumn {

    /**
     * The mandatory property. Here you should specify the foreign key join column.
     *
     * <p>Example. Suppose you have the next DB tables:
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
     * }
     *
     * @Entity
     * @Table(value = "child")
     * public class Child {
     *     @Id private Long id;
     *
     *     @Column private String name;
     *
     *     @ManyToOne
     *     @JoinColumn(name = "parent_id")
     *     private Parent parent;
     * }
     * }</pre>
     *
     * @return
     */
    String name() default "";
}
