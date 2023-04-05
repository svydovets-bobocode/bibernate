package com.bobocode.svydovets.bibernate.action;

import static com.bobocode.svydovets.bibernate.util.EntityUtils.getInsertableFields;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertAction<T> extends AbstractAction<T> {

    public InsertAction(Connection connection, T actionObject) {
        super(connection, actionObject);
    }

    @Override
    protected void doExecute() {
        var actionObjectType = actionObject.getClass();
        try (var preparedStatement =
                connection.prepareStatement(SqlQueryBuilder.createInsertQuery(actionObjectType))) {
            Field[] insertableFields = getInsertableFields(actionObjectType);
            setFieldsInPreparedStatement(preparedStatement, insertableFields);
            preparedStatement.execute();
        } catch (SQLException ex) {
            throw new BibernateException(
                    "Now able to process insert action for entity " + actionObjectType.getSimpleName(), ex);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.INSERT;
    }
}
