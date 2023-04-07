package com.bobocode.svydovets.bibernate.session.service;

import static com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor.executeQueryAndRetrieveResultSet;
import static com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper.moveCursorToNextRow;
import static com.bobocode.svydovets.bibernate.constant.GenerationType.SEQUENCE;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveTableName;
import static java.lang.String.format;

import com.bobocode.svydovets.bibernate.annotation.GeneratedValue;
import com.bobocode.svydovets.bibernate.constant.GenerationType;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.StringUtils;

/**
 * The class implements the IdValuePopulator interface and provides the population of ID values for
 * entities with an ID column annotated with the @GeneratedValue annotation specifying
 * GenerationType.SEQUENCE. <br>
 * <br>
 * The populator retrieves the sequence name and allocation size from the @GeneratedValue
 * annotation, and retrieves a new ID value from the specified sequence.
 */
public class SequenceIdValuePopulator implements IdValuePopulator {
    private static final String SEQUENCE_SQL_REQUEST = "SELECT nextval('%s');";
    private static final String SEQUENCE_COLUMN_NAME = "%s_seq";

    private Map<Class<?>, AtomicLong> latestSequenceValueMap = new HashMap<>();
    private Map<Class<?>, AtomicLong> currentIdMap = new HashMap<>();

    /**
     * Populates the ID value of the given entity object with a new unique value. <br>
     * Executes a SELECT statement to retrieve a new value from the specified sequence. If the
     * allocation size for the sequence has been reached, a new value is selected from the sequence.
     *
     * @param connection the JDBC connection to use for executing the SQL query
     * @param entity the entity object for which to populate the ID value
     * @param <T> the type of the entity object
     * @throws BibernateException if an error occurs while executing the SQL query or populating the
     *     ID value
     */
    @Override
    public <T> void populateIdValue(Connection connection, T entity) {
        var entityType = entity.getClass();
        var latestSequenceValue = latestSequenceValueMap.get(entityType);
        var currentId = currentIdMap.get(entityType);

        Field idField = EntityUtils.resolveIdColumnField(entityType);
        var tableName = resolveTableName(entityType);
        String sequenceName = format(SEQUENCE_COLUMN_NAME, tableName);
        int allocationSize = 1;

        if (idField.isAnnotationPresent(GeneratedValue.class)) {
            var generatedValueAnnotation = idField.getAnnotation(GeneratedValue.class);
            String providedSequenceName = generatedValueAnnotation.sequenceName();
            allocationSize = generatedValueAnnotation.allocationSize();
            if (!StringUtils.isEmpty(providedSequenceName)) {
                sequenceName = providedSequenceName;
            }
        }
        Long idValue =
                getIdValue(
                        connection, entityType, latestSequenceValue, currentId, sequenceName, allocationSize);
        EntityUtils.setIdValueToEntity(entity, idValue);
    }

    @Override
    public GenerationType getType() {
        return SEQUENCE;
    }

    private Long getIdValue(
            Connection connection,
            Class<?> entityType,
            AtomicLong latestSequenceValue,
            AtomicLong currentId,
            String sequenceName,
            int allocationSize) {
        if (latestSequenceValue == null
                || isIdInAllocationSizeRange(latestSequenceValue, currentId, allocationSize)) {
            Long idValue = selectNewValueFromSequence(connection, sequenceName);
            currentId = resetIdToSequenceValue(entityType, idValue);
        }
        return currentId.getAndIncrement();
    }

    private static Long selectNewValueFromSequence(Connection connection, String sequenceName) {
        ResultSet resultSet =
                executeQueryAndRetrieveResultSet(format(SEQUENCE_SQL_REQUEST, sequenceName), connection);
        moveCursorToNextRow(resultSet);
        try {
            return resultSet.getObject(1, Long.class);
        } catch (SQLException e) {
            throw new BibernateException("It's no value returning for sequence: " + sequenceName);
        }
    }

    private AtomicLong resetIdToSequenceValue(Class<?> entityType, Long idValue) {
        AtomicLong latestSequenceValue = new AtomicLong(idValue);
        AtomicLong currentId = new AtomicLong(idValue);
        latestSequenceValueMap.put(entityType, latestSequenceValue);
        currentIdMap.put(entityType, currentId);
        return currentId;
    }

    private static boolean isIdInAllocationSizeRange(
            AtomicLong latestSequenceValue, AtomicLong currentId, int allocationSize) {
        return latestSequenceValue.get() + allocationSize == currentId.get();
    }
}
