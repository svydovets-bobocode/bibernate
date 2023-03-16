package com.bobocode.svydovets.bibernate.util;

import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_MORE_THAN_ONE_ID;
import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ENTITY_ANNOTATION;
import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ID;
import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_UNSUPPORTED_ID_TYPE;
import static java.util.function.Predicate.not;

import com.bobocode.svydovets.bibernate.annotation.Column;
import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityUtils {
    private EntityUtils() {}

    public static void validateEntity(Class<?> type) {
        log.info("Validation {}", type.getName());
        checkIsEntity(type);
        checkHasValidId(type);
        checkHasNoArgConstructor(type);
    }

    private static void checkIsEntity(Class<?> type) {
        if (!type.isAnnotationPresent(Entity.class)) {
            throw new EntityValidationException(
                    String.format(CLASS_HAS_NO_ENTITY_ANNOTATION, type.getName()));
        }
    }

    private static void checkHasValidId(Class<?> type) {
        List<Field> idFields =
                Arrays.stream(type.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Id.class))
                        .toList();

        if (idFields.size() == 0) {
            throw new EntityValidationException(String.format(CLASS_HAS_NO_ID, type.getName()));
        }
        if (idFields.size() > 1) {
            throw new EntityValidationException(
                    String.format(CLASS_HAS_MORE_THAN_ONE_ID, type.getName(), idFields.size()));
        }

        Class<?> idType = idFields.get(0).getType();
        if (!Id.SUPPORTED_OBJECT_TYPES.contains(idType)) {
            throw new EntityValidationException(
                    String.format(CLASS_HAS_UNSUPPORTED_ID_TYPE, type.getName(), idType.getName()));
        }
    }

    private static void checkHasNoArgConstructor(Class<?> type) {
        Arrays.stream(type.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findAny()
                .orElseThrow(
                        () ->
                                new EntityValidationException(
                                        String.format(CLASS_HAS_NO_ARG_CONSTRUCTOR, type.getName())));
    }

    public static String resolveColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::name)
                .filter(not(String::isEmpty))
                .orElseGet(field::getName);
    }

    public static Field[] getInsertableFields(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(EntityUtils::isInsertableField)
                .toArray(Field[]::new);
    }

    private static boolean isInsertableField(Field field) {
        return isInsertableNonId(field) || isNonColumnAnnotatedNonIdField(field);
    }

    private static boolean isInsertableNonId(Field field) {
        return isInsertable(field) && !isIdField(field);
    }

    private static boolean isInsertable(Field field) {
        return field.isAnnotationPresent(Column.class)
                && field.getAnnotation(Column.class).insertable();
    }

    public static Field[] getUpdatableFields(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(EntityUtils::isUpdatableField)
                .toArray(Field[]::new);
    }

    private static boolean isUpdatableField(Field field) {
        return isUpdatableNonId(field) || isNonColumnAnnotatedNonIdField(field);
    }

    private static boolean isUpdatableNonId(Field field) {
        return isUpdatable(field) && !isIdField(field);
    }

    private static boolean isUpdatable(Field field) {
        return field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).updatable();
    }

    private static boolean isNonColumnAnnotatedNonIdField(Field field) {
        return !field.isAnnotationPresent(Column.class) && !isIdField(field);
    }

    private static boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }
}
