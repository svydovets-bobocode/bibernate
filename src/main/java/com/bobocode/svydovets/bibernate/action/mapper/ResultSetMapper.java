package com.bobocode.svydovets.bibernate.action.mapper;

import static com.bobocode.svydovets.bibernate.util.Constants.ERROR_MAPPING_RESULT_SET_TO_OBJECT;

import com.bobocode.svydovets.bibernate.annotation.Column;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;

public class ResultSetMapper {

    public static <T> T mapToObject(Class<T> type, ResultSet resultSet) {
        try {
            return mapResultSetToObject(type, resultSet);
        } catch (Exception e) {
            throw new BibernateException(ERROR_MAPPING_RESULT_SET_TO_OBJECT.formatted(type.getName()), e);
        }
    }

    public static <T> T mapResultSetToObject(Class<T> type, ResultSet resultSet) throws Exception {
        T instance = createEmptyInstance(type);
        while (resultSet.next()) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String columnName = resolveColumnName(field);
                Object compatibleObject =
                        convertObjectToCompatibleType(resultSet.getObject(columnName), field);
                field.set(instance, compatibleObject);
            }
        }
        return instance;
    }

    private static <T> T createEmptyInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create an instance using the default constructor", e);
        }
    }

    private static String resolveColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            String explicitName = field.getAnnotation(Column.class).name();
            if (StringUtils.isNotBlank(explicitName)) {
                return explicitName;
            }
        }
        return field.getName();
    }

    // TODO: handle more specific data types
    private static Object convertObjectToCompatibleType(Object object, Field field) {
        Class<?> fieldType = field.getType();

        if (fieldType.isAssignableFrom(LocalDate.class)) {
            return ((Date) object).toLocalDate();
        }

        if (fieldType.isAssignableFrom(LocalDateTime.class)) {
            return ((Timestamp) object).toLocalDateTime();
        }

        return object;
    }
}
