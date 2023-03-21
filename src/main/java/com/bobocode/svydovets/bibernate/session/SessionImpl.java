package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.constant.ErrorMessage;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.transaction.Transaction;
import com.bobocode.svydovets.bibernate.transaction.TransactionImpl;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionImpl implements Session {

    // todo: replace with Queue<Action>
    private final SelectAction selectAction;
    private final Connection connection;
    private final Transaction transaction;
    private final SqlQueryBuilder sqlQueryBuilder;

    private final Map<EntityKey<?>, Object> entitiesCacheMap = new ConcurrentHashMap<>();
    private final Map<EntityKey<?>, Object[]> entitiesSnapshotMap = new ConcurrentHashMap<>();

    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    public SessionImpl(SelectAction selectAction, Connection connection) {
        this.selectAction = selectAction;
        this.connection = connection;
        this.transaction = new TransactionImpl(connection);
        this.sqlQueryBuilder = new SqlQueryBuilder();
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        verifySessionIsOpened();
        EntityKey<T> entityKey = new EntityKey<>(type, id);
        return type.cast(entitiesCacheMap.computeIfAbsent(entityKey, selectAction::execute));
    }

    @Override
    public <T> T save(T entity) {
        verifySessionIsOpened();
        return null;
    }

    @Override
    public <T> void delete(T id) {
        verifySessionIsOpened();
    }

    @Override
    // todo: integrate with validation
    public <T> List<T> findAll(Class<T> type) {
        verifySessionIsOpened();
        String selectAllQuery = sqlQueryBuilder.createSelectAllQuery(type);
        return retrieveAllFromDb(type, selectAllQuery);
    }

    // todo: move it to the action. Action API must be redesigned
    private <T> List<T> retrieveAllFromDb(Class<T> type, String selectAllQuery) {
        List<T> retrievedEntities = new ArrayList<>();
        try (ResultSet resultSet =
                JdbcExecutor.executeQueryAndRetrieveResultSet(selectAllQuery, connection)) {
            while (ResultSetMapper.moveCursorToNextRow(resultSet)) {
                T loadedEntity = ResultSetMapper.mapToObject(type, resultSet);
                Object id = EntityUtils.retrieveIdValue(loadedEntity);
                EntityKey<T> entityKey = new EntityKey<>(type, id);

                if (entitiesCacheMap.containsKey(entityKey)) {
                    retrievedEntities.add(type.cast(entitiesCacheMap.get(entityKey)));
                } else {
                    entitiesCacheMap.put(entityKey, loadedEntity);
                    // todo: put it to the snapshot map
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
        verifySessionIsOpened();
        return null;
    }

    @Override
    public void close() {
        try {
            flush();
            entitiesCacheMap.clear();
            entitiesSnapshotMap.clear();
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            isOpen.set(false);
        } catch (SQLException e) {
            throw new BibernateException("Something went wrong during closing the session", e);
        }
    }

    @Override
    public <T> T merge(T entity) {
        verifySessionIsOpened();
        return null;
    }

    @Override
    public void detach(Object entity) {
        verifySessionIsOpened();
    }

    @Override
    public void flush() {
        verifySessionIsOpened();
    }

    @Override
    public void begin() {
        verifySessionIsOpened();
        transaction.begin();
    }

    @Override
    public void commit() {
        verifySessionIsOpened();
        flush();
        transaction.commit();
    }

    @Override
    public void rollback() {
        // TODO: think about do we need to clear the persistence context maps
        verifySessionIsOpened();
        transaction.rollback();
    }

    private void verifySessionIsOpened() {
        if (!isOpen.get()) {
            throw new BibernateException(ErrorMessage.SESSION_IS_CLOSED);
        }
    }
}
