package com.bobocode.svydovets.bibernate.annotation;

import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the class is an entity. This annotation is applied to the entity class.
 *
 * <p>It is one of the required parts to make your entities be managed by {@link Session}. If this
 * annotation is absent, and you want to work with this object via the Session - you will face the
 * error.
 *
 * @see Id
 * @see RequiredAnnotationValidatorProcessor
 * @see Session
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {}
