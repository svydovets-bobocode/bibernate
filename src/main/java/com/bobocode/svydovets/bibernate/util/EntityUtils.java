package com.bobocode.svydovets.bibernate.util;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityUtils {
    private EntityUtils() {}

    public static <T> void validateEntity(Class<T> type) {
        log.info("Validation %s".formatted(type.getName()));
        isEntity(type);
        //        todo: check that the class entity has at least one id
        hasNoArgConstructor(type);
    }

    private static <T> void isEntity(Class<T> type) {
        if (!type.isAnnotationPresent(Entity.class)) {
            throw new EntityValidationException(
                    "Class '"
                            + type.getName()
                            + "' has no @Entity annotation"
                            + " (every entity class must be annotated with '@Entity')");
        }
    }

    private static <T> void hasNoArgConstructor(Class<T> type) {
        Arrays.stream(type.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny()
                .orElseThrow(
                        () ->
                                new EntityValidationException(
                                        "Entity '"
                                                + type.getName()
                                                + "' has no 'no-arg constructor'"
                                                + " (every '@Entity' class must declare 'no-arg constructor')"));
    }
}
