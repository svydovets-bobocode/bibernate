package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class SessionImpl implements Session {

    private final DataSource dataSource;
    private final SelectAction selectAction;

    private final SqlQueryBuilder sqlQueryBuilder = new SqlQueryBuilder();
    private final Map<EntityKey<?>, Object> persistenceContext = new ConcurrentHashMap<>();
    private final Map<EntityKey<?>, Object[]> entitiesSnapshotMap = new ConcurrentHashMap<>();

    @Override
    public <T> T find(Class<T> type, Object id) {
        EntityUtils.validateEntity(type);
        EntityKey<T> entityKey = new EntityKey<>(type, id);
        return type.cast(persistenceContext.computeIfAbsent(entityKey, selectAction::execute));
    }

    @Override
    public <T> T save(T entity) {
        return null;
    }

    @Override
    public <T> void delete(T id) {
    }

    @Override
    public <T> List<T> findAll(Class<T> type) {
        EntityUtils.validateEntity(type);
        String selectAllQuery = sqlQueryBuilder.createSelectAllQuery(type);
        return retrieveAllFromDb(type, selectAllQuery);
    }

    private <T> List<T> retrieveAllFromDb(Class<T> type, String selectAllQuery) {
        List<T> retrievedEntities = new ArrayList<>();
        try (ResultSet resultSet = JdbcExecutor.executeQueryAndRetrieveResultSet(selectAllQuery, dataSource)) {
            while (resultSet.next()) {
                T loadedEntity = ResultSetMapper.mapToObject(type, resultSet);
                Object id = EntityUtils.retrieveIdValue(loadedEntity);
                EntityKey<T> entityKey = new EntityKey<>(type, id);

                if (persistenceContext.containsKey(entityKey)) {
                    retrievedEntities.add(type.cast(persistenceContext.get(entityKey)));
                } else {
                    persistenceContext.put(entityKey, loadedEntity);
                    //todo: put it to the snapshot map
                    retrievedEntities.add(loadedEntity);
                }
            }
        } catch (SQLException e) {
            throw new BibernateException("", e);
        }
        return retrievedEntities;
    }

    @Override
    public <T> List<T> findAll(Class<T> type, Map<String, Object> properties) {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public <T> T merge(T entity) {
        return null;
    }

    @Override
    public void detach(Object entity) {}

    @Override
    public void flush() {}
}
