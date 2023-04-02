package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import java.sql.Connection;
import java.sql.SQLException;

public class DeleteAction<T> extends AbstractAction<T> {
    private final RequiredAnnotationValidatorProcessor validatorProcessor;

    public DeleteAction(Connection connection, T actionObject) {
        super(connection, actionObject);
        this.validatorProcessor = new RequiredAnnotationValidatorProcessorImpl();
    }

    @Override
    protected void doExecute() {
        var id = EntityUtils.getIdValue(actionObject);
        var type = actionObject.getClass();
        validatorProcessor.validate(type, Operation.DELETE);
        String deleteByIdQuery = SqlQueryBuilder.createDeleteByIdQuery(type);
        try (var statement = connection.prepareStatement(deleteByIdQuery)) {
            statement.setObject(1, id);
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
