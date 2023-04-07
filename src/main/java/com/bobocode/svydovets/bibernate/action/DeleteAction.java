package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * The DeleteAction class represents a delete action to delete an object from the database. It
 * extends the AbstractAction class.
 *
 * @param <T> the type of object being deleted
 */
public class DeleteAction<T> extends AbstractAction<T> {
    private static final int ID_PARAM_INDEX = 1;
    private static final int VERSION_PARAM_INDEX = 2;

    private final RequiredAnnotationValidatorProcessor validatorProcessor;

    public DeleteAction(Connection connection, T actionObject) {
        super(connection, actionObject);
        this.validatorProcessor = new RequiredAnnotationValidatorProcessorImpl();
    }

    /**
     * Executes the delete action by constructing and executing a SQL DELETE statement for the
     * corresponding entity. If the entity has a version field, the statement will include a WHERE
     * condition for the version field as well.
     *
     * @throws BibernateException if the SQL DELETE statement fails to delete exactly one row.
     * @throws ConnectionException if there is an error with the connection while executing the SQL
     *     statement.
     */
    @Override
    protected void doExecute() {
        var id = EntityUtils.getIdValue(actionObject);
        var type = actionObject.getClass();
        validatorProcessor.validate(type, Operation.DELETE);
        String deleteByIdQuery = SqlQueryBuilder.createDeleteByIdQuery(type);

        Optional<Field> optVersionField = EntityUtils.findVersionField(actionObject.getClass());
        if (optVersionField.isPresent()) {
            deleteByIdQuery =
                    SqlQueryBuilder.addVersionToWhereConditionIfNeeds(
                            deleteByIdQuery, actionObject.getClass());
        }

        try (var statement = connection.prepareStatement(deleteByIdQuery)) {
            statement.setObject(ID_PARAM_INDEX, id);

            if (optVersionField.isPresent()) {
                Object versionValue = EntityUtils.getVersionValue(actionObject, optVersionField.get());
                statement.setObject(VERSION_PARAM_INDEX, versionValue);
            }

            if (statement.executeUpdate() != 1) {
                throw new BibernateException(
                        "Unable to delete entity: %s by id: %s".formatted(type.getSimpleName(), id));
            }
        } catch (SQLException e) {
            throw new ConnectionException("Error while executing query %s".formatted(deleteByIdQuery), e);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.REMOVE;
    }
}
