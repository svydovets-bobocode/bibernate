package com.bobocode.svydovets.bibernate.util;

import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ENTITY_ANNOTATION;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityUtils {
    private EntityUtils() {}

    public static void validateEntity(Class<?> type) {
        log.info("Validation {}", type.getName());
        checkIsEntity(type);
        //        todo: check that the class entity has at least one @Id
        checkHasNoArgConstructor(type);
    }

    private static void checkIsEntity(Class<?> type) {
        if (!type.isAnnotationPresent(Entity.class)) {
            log.error("{} has no @Entity annotation", type.getName());
            throw new EntityValidationException(
                    String.format(CLASS_HAS_NO_ENTITY_ANNOTATION, type.getName()));
        }
    }

    private static void checkHasNoArgConstructor(Class<?> type) {
        Arrays.stream(type.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny()
                .orElseThrow(
                        () -> {
                            log.error("{} has no 'no-arg constructor'", type.getName());
                            return new EntityValidationException(
                                    String.format(CLASS_HAS_NO_ARG_CONSTRUCTOR, type.getName()));
                        });
    }
}
