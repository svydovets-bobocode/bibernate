package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import java.sql.Connection;
import java.sql.SQLException;

public class DeleteAction {
    private final RequiredAnnotationValidatorProcessor validatorProcessor;
    private final Connection connection;

    public DeleteAction(Connection connection) {
        this.connection = connection;
        this.validatorProcessor = new RequiredAnnotationValidatorProcessorImpl();
    }

    public <T> void execute(EntityKey<T> entityKey) {
        var type = entityKey.type();
        var id = entityKey.id();
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
}
