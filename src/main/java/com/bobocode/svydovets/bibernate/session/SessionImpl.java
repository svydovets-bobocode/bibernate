package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionImpl implements Session {
    private final SelectAction selectAction;
    private final Map<EntityKey<?>, Object> cache = new ConcurrentHashMap<>();

    @Override
    public <T> T find(Class<T> type, Object id) {
        EntityKey<T> entityKey = new EntityKey<>(type, id);
        return type.cast(cache.computeIfAbsent(entityKey, selectAction::execute));
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
    public void close() {}

    @Override
    public <T> T merge(T entity) {
        return null;
    }

    @Override
    public void detach(Object entity) {}

    @Override
    public void flush() {}
}
