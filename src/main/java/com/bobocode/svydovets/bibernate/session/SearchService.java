package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchService {
    private final Connection connection;
    private final RequiredAnnotationValidatorProcessor validatorProcessor;

    public SearchService(Connection connection) {
        this.connection = connection;
        this.validatorProcessor = new RequiredAnnotationValidatorProcessorImpl();
    }

    public <T> T findOne(EntityKey<T> key) {
        var type = key.type();
        var id = key.id();
        validatorProcessor.validate(type, Operation.SELECT);
        String selectByIdQuery = SqlQueryBuilder.createSelectByIdQuery(type);
        try (var statement = connection.prepareStatement(selectByIdQuery)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (ResultSetMapper.moveCursorToNextRow(resultSet)) {
                T result = ResultSetMapper.mapToObject(type, resultSet);
                log.debug("Mapped result set to object: {}", result);
                return result;
            } else {
                // Todo: add Entity not found exception
                throw new BibernateException(
                        "Unable to find entity: %s by id: %s".formatted(type.getSimpleName(), id));
            }
        } catch (Exception e) {
            throw new ConnectionException("Error while executing query %s".formatted(selectByIdQuery), e);
        }
    }

    public <T> Map<EntityKey<T>, T> findAllByType(Class<T> type) {
        validatorProcessor.validate(type, Operation.SELECT);
        Map<EntityKey<T>, T> loadedEntitiesMap = new HashMap<>();
        String selectAllQuery = SqlQueryBuilder.createSelectAllQuery(type);
        try (ResultSet resultSet =
                JdbcExecutor.executeQueryAndRetrieveResultSet(selectAllQuery, connection)) {
            while (ResultSetMapper.moveCursorToNextRow(resultSet)) {
                T loadedEntity = ResultSetMapper.mapToObject(type, resultSet);
                Optional<?> id = EntityUtils.retrieveIdValue(loadedEntity);
                EntityKey<T> entityKey = new EntityKey<>(type, id.orElse(null));
                loadedEntitiesMap.put(entityKey, loadedEntity);
            }
            return loadedEntitiesMap;
        } catch (Exception e) {
            throw new BibernateException(
                    "Unable to findAll entities: %s ".formatted(type.getSimpleName()));
        }
    }
}
