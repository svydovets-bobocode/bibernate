package com.bobocode.svydovets.bibernate.action.mapper;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_MAPPING_RESULT_SET_TO_OBJECT;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ResultSetMapper {

    public static boolean moveCursorToNextRow(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new ConnectionException("Error while executing 'next()' on the ResultSet", e);
        }
    }

    public static <T> T mapToObject(Class<T> type, ResultSet resultSet) {
        try {
            return mapResultSetToObject(type, resultSet);
        } catch (Exception e) {
            throw new BibernateException(ERROR_MAPPING_RESULT_SET_TO_OBJECT.formatted(type.getName()), e);
        }
    }

    private static <T> T mapResultSetToObject(Class<T> type, ResultSet resultSet)
            throws SQLException {
        T instance = EntityUtils.createEmptyInstance(type);
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            String columnName = EntityUtils.resolveColumnName(field);
            Object compatibleObject =
                    convertObjectToCompatibleType(resultSet.getObject(columnName), field);
            EntityUtils.setValueToField(instance, field, compatibleObject);
        }
        return instance;
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
