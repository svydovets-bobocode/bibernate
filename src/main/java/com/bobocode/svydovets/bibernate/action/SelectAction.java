package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SelectAction implements Action {
    private final DataSource dataSource;
    private final SqlQueryBuilder sqlQueryBuilder;
    private final RequiredAnnotationValidatorProcessor validatorProcessor;

    public SelectAction(DataSource dataSource, SqlQueryBuilder sqlQueryBuilder) {
        this.dataSource = dataSource;
        this.sqlQueryBuilder = sqlQueryBuilder;
        this.validatorProcessor = new RequiredAnnotationValidatorProcessorImpl();
    }

    @Override
    public <T> T execute(EntityKey<T> key) {
        validatorProcessor.validate(key.type(), Operation.SELECT);
        String selectByIdQuery = sqlQueryBuilder.createSelectByIdQuery(key.type());
        return processExecutingQuery(key, selectByIdQuery);
    }

    private <T> T processExecutingQuery(EntityKey<T> key, String selectByIdQuery) {
        log.debug("Executing query: {}", selectByIdQuery);
        return JdbcExecutor.executeQuery(
                dataSource,
                selectByIdQuery,
                statement -> {
                    try {
                        // Todo: create this method more generic, and use it for different queries
                        statement.setObject(1, key.id());
                        log.debug("Setting statement parameter: {}", key.id());
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to set statement parameters", e);
                    }
                },
                resultSet -> {
                    T result = ResultSetMapper.mapToObject(key.type(), resultSet);
                    log.debug("Mapped result set to object: {}", result);
                    return result;
                });
    }
}
