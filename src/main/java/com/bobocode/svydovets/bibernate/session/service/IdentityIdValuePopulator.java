package com.bobocode.svydovets.bibernate.session.service;

import static com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor.executeQueryAndRetrieveResultSet;
import static com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper.moveCursorToNextRow;
import static com.bobocode.svydovets.bibernate.constant.GenerationType.IDENTITY;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveIdColumnField;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveIdColumnName;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveTableName;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.setIdValueToEntity;
import static java.lang.String.format;

import com.bobocode.svydovets.bibernate.constant.GenerationType;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IdentityIdValuePopulator implements IdValuePopulator {
    private static final String SEQUENCE_SQL_REQUEST = "SELECT nextval('%s');";
    private static final String SEQUENCE_COLUMN_NAME = "%s_%s_seq";

    @Override
    public <T> void populateIdValue(Connection connection, T entity) {
        var entityType = entity.getClass();
        var tableName = resolveTableName(entityType);
        var idColumName = resolveIdColumnName(entityType);
        var sequenceName = format(SEQUENCE_COLUMN_NAME, tableName, idColumName);
        ResultSet resultSet =
                executeQueryAndRetrieveResultSet(format(SEQUENCE_SQL_REQUEST, sequenceName), connection);
        moveCursorToNextRow(resultSet);
        var idColumnField = resolveIdColumnField(entityType);
        Object idValue;
        try {
            idValue = resultSet.getObject(1, idColumnField.getType());
        } catch (SQLException e) {
            throw new BibernateException("It's no value returning for sequence: " + sequenceName);
        }
        setIdValueToEntity(entity, idValue);
    }

    @Override
    public GenerationType getType() {
        return IDENTITY;
    }
}
