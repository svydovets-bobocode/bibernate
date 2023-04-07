package com.bobocode.svydovets.bibernate.session.service;

import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;

import com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import com.bobocode.svydovets.bibernate.exception.EntityNotFoundException;
import com.bobocode.svydovets.bibernate.session.LockModeType;
import com.bobocode.svydovets.bibernate.state.EntityStateService;
import com.bobocode.svydovets.bibernate.state.EntityStateServiceImpl;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchService {
    private final Connection connection;
    private final RequiredAnnotationValidatorProcessor validatorProcessor;
    private final EntityStateService entityStateService;

    @Setter private ResultSetMapper resultSetMapper;

    public SearchService(Connection connection) {
        this.connection = connection;
        this.validatorProcessor = new RequiredAnnotationValidatorProcessorImpl();
        this.entityStateService = new EntityStateServiceImpl();
    }

    public <T> T findOne(EntityKey<T> key) {
        return findOne(key, LockModeType.NONE);
    }

    public <T> T findOne(EntityKey<T> key, LockModeType lockModeType) {
        var type = key.type();
        var id = key.id();
        validatorProcessor.validate(type, Operation.SELECT);
        String selectByIdQuery = SqlQueryBuilder.createSelectByIdQuery(type, lockModeType);
        try (var statement = connection.prepareStatement(selectByIdQuery)) {
            statement.setObject(1, id);
            ResultSet resultSet = JdbcExecutor.executePreparedStatementAndRetrieveResultSet(statement);
            if (ResultSetMapper.moveCursorToNextRow(resultSet)) {
                T result = resultSetMapper.mapToObject(type, resultSet);
                log.debug("Mapped result set to object: {}", result);
                return result;
            } else {
                throw new EntityNotFoundException(
                        "Unable to find entity: %s by id: %s".formatted(type.getSimpleName(), id));
            }
        } catch (SQLException e) {
            throw new ConnectionException("Error while executing query %s".formatted(selectByIdQuery), e);
        }
    }

    public <T> List<T> findAllByType(
            Class<T> type,
            Map<EntityKey<?>, Object> entitiesCacheMap,
            Map<EntityKey<?>, Map<String, Object>> entitiesSnapshotMap) {

        validatorProcessor.validate(type, Operation.SELECT);
        List<T> retrievedEntities = new ArrayList<>();
        String selectAllQuery = SqlQueryBuilder.createSelectAllQuery(type);
        try (ResultSet resultSet =
                JdbcExecutor.executeQueryAndRetrieveResultSet(selectAllQuery, connection)) {
            while (ResultSetMapper.moveCursorToNextRow(resultSet)) {
                T loadedEntity = resultSetMapper.mapToObject(type, resultSet);
                Object id = EntityUtils.getIdValue(loadedEntity);
                EntityKey<T> entityKey = new EntityKey<>(type, id);

                if (entitiesCacheMap.containsKey(entityKey)) {
                    retrievedEntities.add(type.cast(entitiesCacheMap.get(entityKey)));
                } else {
                    entitiesCacheMap.put(entityKey, loadedEntity);
                    entitiesSnapshotMap.put(entityKey, EntityUtils.getFieldValuesFromEntity(loadedEntity));
                    entityStateService.setEntityState(entityKey, MANAGED);
                    retrievedEntities.add(loadedEntity);
                }
            }
            return retrievedEntities;
        } catch (Exception e) {
            throw new BibernateException(
                    "Unable to findAll entities: %s ".formatted(type.getSimpleName()));
        }
    }
}
