package com.bobocode.svydovets.bibernate.action;

import static com.bobocode.svydovets.bibernate.annotation.Version.INITIAL_VERSION_FIELD_VALUE;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.getInsertableFields;

import com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/** Represents an action to insert an object of type T into the database. */
@Slf4j
public class InsertAction<T> extends AbstractAction<T> {

    public InsertAction(Connection connection, T actionObject) {
        super(connection, actionObject);
    }

    /**
     * Executes the insert action by creating a prepared statement using the insert query built with
     * SqlQueryBuilder.createInsertQuery(), setting the values of insertable fields in the prepared
     * statement using setFieldsInPreparedStatement(), and executing the statement.
     *
     * @throws BibernateException if the SQLException occurs.
     */
    @Override
    protected void doExecute() {
        var actionObjectType = actionObject.getClass();
        try (var preparedStatement =
                connection.prepareStatement(SqlQueryBuilder.createInsertQuery(actionObjectType))) {
            Field[] insertableFields = getInsertableFields(actionObjectType);
            setFieldsInPreparedStatement(preparedStatement, insertableFields);
            JdbcExecutor.executePreparedStatement(preparedStatement);
        } catch (SQLException ex) {
            throw new BibernateException(
                    "Unable to execute update action for " + actionObjectType.getSimpleName(), ex);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.INSERT;
    }

    @Override
    protected long resolveVersionField(Object optFieldValue) {
        return INITIAL_VERSION_FIELD_VALUE;
    }
}
