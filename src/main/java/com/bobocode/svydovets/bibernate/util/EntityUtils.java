package com.bobocode.svydovets.bibernate.util;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ENTITY_ANNOTATION;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ID;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_GETTING_FIELD_VALUES_FROM_ENTITY;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_RETRIEVING_VALUE_FROM_FIELD;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_SETTING_VALUE_TO_FIELD;

import com.bobocode.svydovets.bibernate.annotation.Column;
import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.annotation.Table;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class EntityUtils {
    private EntityUtils() {}

    public static void checkIsEntity(Class<?> type) {
        if (!type.isAnnotationPresent(Entity.class)) {
            throw new EntityValidationException(
                    String.format(CLASS_HAS_NO_ENTITY_ANNOTATION, type.getName()));
        }
    }

    public static void checkHasNoArgConstructor(Class<?> type) {
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
                .filter(StringUtils::isNotBlank)
                .orElseGet(field::getName);
    }

    public static String resolveIdColumnName(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(EntityUtils::isIdField)
                .findAny()
                .map(EntityUtils::resolveColumnName)
                .orElseThrow(
                        () ->
                                new EntityValidationException(
                                        String.format(CLASS_HAS_NO_ARG_CONSTRUCTOR, type.getName())));
    }

    public static Field[] getInsertableFields(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(EntityUtils::isInsertableField)
                .toArray(Field[]::new);
    }

    public static Field[] getUpdatableFields(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(EntityUtils::isUpdatableField)
                .toArray(Field[]::new);
    }

    public static boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
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

    public static <T> T createEmptyInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance using the default constructor", e);
        }
    }

    public static String resolveTableName(Class<?> entityType) {
        log.trace("Resolving table name for entity {}", entityType);
        if (entityType.isAnnotationPresent(Table.class)) {
            String explicitName = entityType.getDeclaredAnnotation(Table.class).value();
            if (StringUtils.isNotBlank(explicitName)) {
                log.trace("Table is specified explicitly as {}", explicitName);
                return explicitName;
            }
        }
        String tableName = entityType.getSimpleName().toLowerCase();
        log.trace("Table is explicitly specified, falling back to call name {}", tableName);
        return tableName;
    }

    public static <T> Optional<Object> retrieveIdValue(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(EntityUtils::isIdField)
                .findAny()
                .map(field -> retrieveValueFromField(entity, field))
                .orElseThrow(() -> new EntityValidationException(CLASS_HAS_NO_ID));
    }

    public static <T> Optional<Object> retrieveValueFromField(T entity, Field field) {
        try {
            field.setAccessible(true);
            return Optional.ofNullable(field.get(entity));
        } catch (Exception e) {
            throw new BibernateException(
                    String.format(
                            ERROR_RETRIEVING_VALUE_FROM_FIELD, field.getName(), entity.getClass().getName()),
                    e);
        }
    }

    public static <T> void setValueToField(T instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            throw new BibernateException(
                    String.format(
                            ERROR_SETTING_VALUE_TO_FIELD, value, field.getType(), instance.getClass().getName()),
                    e);
        }
    }

    public static <T> void updateManagedEntityField(T fromEntity, T toEntity, Field field) {
        Object detachedValue = null;
        try {
            field.setAccessible(true);
            detachedValue = field.get(fromEntity);
            if (detachedValue != null) {
                field.set(toEntity, detachedValue);
            }
        } catch (IllegalAccessException e) {
            throw new BibernateException(
                    String.format(
                            ERROR_SETTING_VALUE_TO_FIELD,
                            detachedValue,
                            field.getType(),
                            toEntity.getClass().getName()),
                    e);
        }
    }

    public static <T> Object[] getFieldValuesFromEntity(T entity) {
        try {
            Field[] fields = entity.getClass().getDeclaredFields();
            return Arrays.stream(fields)
                    .map(field -> retrieveValueFromField(entity, field))
                    .map(Optional::orElseThrow)
                    .toArray();
        } catch (Exception e) {
            throw new BibernateException(
                    String.format(ERROR_GETTING_FIELD_VALUES_FROM_ENTITY, entity.getClass().getName()), e);
        }
    }
}
