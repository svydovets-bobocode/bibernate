package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class UpdateAction<T> extends AbstractAction<T> {

    public UpdateAction(Connection connection, T actionObject) {
        super(connection, actionObject);
    }

    @Override
    protected void doExecute() {
        var actionObjectType = actionObject.getClass();
        String updateQuery = SqlQueryBuilder.createUpdateQuery(actionObjectType);

        Optional<Field> optionalVersionField = EntityUtils.findVersionField(actionObject.getClass());
        if (optionalVersionField.isPresent()) {
            updateQuery =
                    SqlQueryBuilder.addVersionToWhereConditionIfNeeds(updateQuery, actionObject.getClass());
        }

        try (var preparedStatement = connection.prepareStatement(updateQuery)) {
            Field[] updatableFields = EntityUtils.getUpdatableFields(actionObjectType);

            setFieldsInPreparedStatement(preparedStatement, updatableFields);

            Object id = EntityUtils.getIdValue(actionObject);
            preparedStatement.setObject(updatableFields.length + 1, id);

            if (optionalVersionField.isPresent()) {
                Object inWhereCondition =
                        EntityUtils.getVersionValue(actionObject, optionalVersionField.get());
                preparedStatement.setObject(updatableFields.length + 2, inWhereCondition);
            }

            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows != 1) {
                throw new BibernateException(
                        "Unable to update entity: %s with id: %s"
                                .formatted(actionObjectType.getSimpleName(), id));
            }
        } catch (SQLException ex) {
            throw new BibernateException(ex);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.UPDATE;
    }
}
