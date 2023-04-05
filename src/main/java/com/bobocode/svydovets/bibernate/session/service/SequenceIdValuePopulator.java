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

public class SequenceIdValuePopulator implements IdValuePopulator {
    private static final String SEQUENCE_SQL_REQUEST = "SELECT nextval('%s');";
    private static final String SEQUENCE_COLUMN_NAME = "%s_seq";

    private Map<Class<?>, AtomicLong> latestSequenceValueMap = new HashMap<>();
    private Map<Class<?>, AtomicLong> currentIdMap = new HashMap<>();

    @Override
    public <T> void populateIdValue(Connection connection, T entity) {
        var entityType = entity.getClass();
        var latestSequenceValue = latestSequenceValueMap.get(entityType);
        var currentId = currentIdMap.get(entityType);

        Field idField = EntityUtils.resolveIdColumnField(entityType);
        var tableName = resolveTableName(entityType);
        String sequenceName = format(SEQUENCE_COLUMN_NAME, tableName);
        int allocationSize = 50;

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
