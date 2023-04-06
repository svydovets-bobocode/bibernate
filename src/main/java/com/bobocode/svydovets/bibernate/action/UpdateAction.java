package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * A class that represents an update action to be performed on a database entity.
 * The action updates the fields of the entity with the values in the provided actionObject
 * according to the corresponding database table row that has the same ID as the entity.
 */
public class UpdateAction<T> extends AbstractAction<T> {

    public UpdateAction(Connection connection, T actionObject) {
        super(connection, actionObject);
    }

    /**
     * Executes the update action on the database by updating the corresponding row of the provided entity object.
     * The method creates an SQL update query using the SqlQueryBuilder class and sets the values of the entity's
     * updatable fields to the corresponding placeholders in the query. If the entity has a version field, the query
     * is modified to include the version field in the where condition. The method then executes the prepared statement
     * and throws an exception if the number of updated rows is not exactly one.
     *
     @throws BibernateException if there is an error executing the update query
     */
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
