package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.transaction.Transaction;
import com.bobocode.svydovets.bibernate.transaction.TransactionImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionImpl implements Session {
    private SelectAction selectAction;
    private final Connection connection;
    private Transaction transaction;
    private boolean isOpen;
    private final Map<EntityKey<?>, Object> persistenceContext = new ConcurrentHashMap<>();
    private final Map<EntityKey<?>, Object[]> entitiesSnapshotMap = new ConcurrentHashMap<>();

    public SessionImpl(SelectAction selectAction, Connection connection) {
        this.selectAction = selectAction;
        this.connection = connection;
        this.transaction = new TransactionImpl(connection);
        this.isOpen = true;
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        EntityKey<T> entityKey = new EntityKey<>(type, id);
        return type.cast(persistenceContext.computeIfAbsent(entityKey, selectAction::execute));
    }

    @Override
    public <T> T save(T entity) {
        return null;
    }

    @Override
    public <T> void delete(T id) {}

    @Override
    public <T> List<T> findAll(Class<T> type) {
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> type, Map<String, Object> properties) {
        return null;
    }

    @Override
    public void close() {
        try {
            isOpen = false;
            connection.close();
        } catch (SQLException e) {
            throw new BibernateException("Unable to close session", e);
        }
    }

    @Override
    public <T> T merge(T entity) {
        return null;
    }

    @Override
    public void detach(Object entity) {}

    @Override
    public void flush() {}

    @Override
    public void begin() {
        checkIsOpen();
        transaction.begin();
    }

    @Override
    public void commit() {
        checkIsOpen();
        // todo: flush()???
        transaction.commit();
    }

    @Override
    public void rollback() {
        checkIsOpen();
        transaction.rollback();
    }

    private void checkIsOpen() {
        if (!isOpen) {
            throw new BibernateException("Session is already closed");
        }
    }
}
