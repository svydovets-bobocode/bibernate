package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SelectAction {
    private final Connection connection;
    private final SqlQueryBuilder sqlQueryBuilder;
    private final RequiredAnnotationValidatorProcessor validatorProcessor;

    public SelectAction(Connection connection, SqlQueryBuilder sqlQueryBuilder) {
        this.connection = connection;
        this.sqlQueryBuilder = sqlQueryBuilder;
        this.validatorProcessor = new RequiredAnnotationValidatorProcessorImpl();
    }

    public <T> T execute(EntityKey<T> key) {
        var type = key.type();
        var id = key.id();
        validatorProcessor.validate(type, Operation.SELECT);
        String selectByIdQuery = sqlQueryBuilder.createSelectByIdQuery(type);

        try (var statement = connection.prepareStatement(selectByIdQuery)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMapper.moveCursorToNextRow(resultSet);
            T result = ResultSetMapper.mapToObject(type, resultSet);
            log.debug("Mapped result set to object: {}", result);
            return result;
        } catch (SQLException e) {
            throw new BibernateException(
                    String.format("Unable to find entity: %s by id: %s:", type, id), e);
        }
    }
}
