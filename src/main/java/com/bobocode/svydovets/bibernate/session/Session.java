package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class Session {
    private final SelectAction selectAction;
    private final Map<EntityKey<?>, Object> cache = new ConcurrentHashMap<>();

    public <T> T find(Class<T> type, Object id) {
        EntityKey<T> entityKey = new EntityKey<>(type, id);
        return type.cast(cache.computeIfAbsent(entityKey, selectAction::execute));
    }
}
