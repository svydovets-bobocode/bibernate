package com.bobocode.svydovets.bibernate.action.mapper;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_MAPPING_RESULT_SET_TO_OBJECT;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import com.bobocode.svydovets.bibernate.lazy.SvydovetsLazyList;
import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

/** The ResultSetMapper class provides methods for mapping a JDBC ResultSet to Java objects. */
@RequiredArgsConstructor
public class ResultSetMapper {

    private final Session session;

    /**
     * Moves the cursor to the next row in the specified ResultSet.
     *
     * @param resultSet the ResultSet to move the cursor on
     * @return true if there is a next row; false otherwise
     * @throws ConnectionException if there is an error while moving the cursor
     */
    public static boolean moveCursorToNextRow(ResultSet resultSet) {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new ConnectionException("Error while executing 'next()' on the ResultSet", e);
        }
    }

    /**
     * Maps the current row of the specified ResultSet to a Java object of the specified type.
     *
     * @param type the type of the object to map the ResultSet row to
     * @param resultSet the ResultSet to map to the Java object
     * @param <T> the type of the object to map the ResultSet row to
     * @return the Java object obtained by mapping the ResultSet row
     * @throws BibernateException if there is an error while mapping the ResultSet row to a Java
     *     object
     */
    public <T> T mapToObject(Class<T> type, ResultSet resultSet) {
        try {
            return mapResultSetToObject(type, resultSet);
        } catch (Exception e) {
            throw new BibernateException(ERROR_MAPPING_RESULT_SET_TO_OBJECT.formatted(type.getName()), e);
        }
    }

    /**
     * Maps a {@link ResultSet} to an object of the given type using the provided {@link EntityUtils}
     * and {@link Session}. The method iterates over the fields of the given type, mapping simple
     * column fields to their corresponding columns in the ResultSet, and entity fields to related
     * entities in the database.
     *
     * @param <T> the type of object to map the ResultSet to
     * @param type the type of object to map the ResultSet to
     * @param resultSet the ResultSet to map to an object
     * @return an object of the given type mapped from the ResultSet
     * @throws SQLException if an error occurs while accessing the ResultSet
     * @throws BibernateException if an error occurs while mapping the ResultSet to an object
     */
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
            } else if (EntityUtils.isEntityCollectionField(field)) {
                List<T> list = createSvydovetsLazyCollection(type, field, instance);
                EntityUtils.setValueToField(instance, field, list);
            }
        }
        return instance;
    }

    private <T> SvydovetsLazyList<T> createSvydovetsLazyCollection(
            Class<T> type, Field field, T instance) {
        var entityType = EntityUtils.getEntityCollectionElementType(field);
        Field relatedEntityField = EntityUtils.getRelatedEntityField(type, entityType);
        Object idValue = EntityUtils.getIdValue(instance);
        Supplier<List<T>> collectionSupplier =
                () -> {
                    session.verifySessionIsOpened();
                    return (List<T>) session.findAllBy(entityType, relatedEntityField, idValue);
                };
        return new SvydovetsLazyList<>(collectionSupplier);
    }

    /**
     * Converts the specified object to a compatible type based on the type of the specified field.
     *
     * @param object the object to convert to a compatible type
     * @param field the field that specifies the type to convert the object to
     * @return the converted object
     */
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
