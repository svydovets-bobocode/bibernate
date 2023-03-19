package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.util.Constants;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionImpl implements Session {
    private final DataSource dataSource;
    private final SelectAction selectAction;
    private final Map<EntityKey<?>, Object> persistenceContext = new ConcurrentHashMap<>();
    private final Map<EntityKey<?>, Object[]> entitiesSnapshotMap = new ConcurrentHashMap<>();

    private AtomicBoolean opened = new AtomicBoolean(true);

    @Override
    public <T> T find(Class<T> type, Object id) {
        verifySessionIsOpened();
        EntityUtils.validateEntity(type);
        EntityKey<T> entityKey = new EntityKey<>(type, id);
        return type.cast(persistenceContext.computeIfAbsent(entityKey, selectAction::execute));
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
    public <T> List<T> findAll(Class<T> type) {
        verifySessionIsOpened();
        return null;
    }

    @Override
    public <T> List<T> findAll(Class<T> type, Map<String, Object> properties) {
        verifySessionIsOpened();
        return null;
    }

    @Override
    public void close() {
        try {
            persistenceContext.clear();
            entitiesSnapshotMap.clear();
            Connection connection = dataSource.getConnection();
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            opened.set(false);
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

    private void verifySessionIsOpened() {
        if (!opened.get()) {
            throw new BibernateException(Constants.SESSION_IS_CLOSED);
        }
    }
}
