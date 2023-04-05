package com.bobocode.svydovets.bibernate.action.mapper;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_MAPPING_RESULT_SET_TO_OBJECT;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResultSetMapper {

    private final Session session;

    public static boolean moveCursorToNextRow(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new ConnectionException("Error while executing 'next()' on the ResultSet", e);
        }
    }

    public <T> T mapToObject(Class<T> type, ResultSet resultSet) {
        try {
            return mapResultSetToObject(type, resultSet);
        } catch (Exception e) {
            throw new BibernateException(ERROR_MAPPING_RESULT_SET_TO_OBJECT.formatted(type.getName()), e);
        }
    }

    // todo: @ManyToOne relations
    // + todo 1: add util methods isRegularField, isRelationsField(isEntityField, isEntityCollection)
    // + todo 2: process within the ResultSetMapper.mapToObject
    // + todo 3: if is entity field: retrieve the column name from the JoinColumn
    // + todo 4: retrieve the ID value from the ResultSet
    // + todo 5: findById and related entity class and set it to the entity object
    // + todo 6: change the snapshot saving logic to save the ID of the related entity
    private <T> T mapResultSetToObject(Class<T> type, ResultSet resultSet) throws SQLException {
        T instance = EntityUtils.createEmptyInstance(type);
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (EntityUtils.isSimpleColumnField(field)) {
                String columnName = EntityUtils.resolveColumnName(field);
                Object compatibleObject =
                        convertObjectToCompatibleType(resultSet.getObject(columnName), field);
                EntityUtils.setValueToField(instance, field, compatibleObject);
            } else if (EntityUtils.isEntityField(field)) {
                String foreignKeyColumnName = EntityUtils.resolveJoinColumnName(field);
                Object relatedEntityId = resultSet.getObject(foreignKeyColumnName);
                Object relatedEntity = session.find(field.getType(), relatedEntityId);
                EntityUtils.setValueToField(instance, field, relatedEntity);
            }
        }
        return instance;
    }

    private Object convertObjectToCompatibleType(Object object, Field field) {
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
