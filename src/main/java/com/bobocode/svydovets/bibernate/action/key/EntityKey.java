package com.bobocode.svydovets.bibernate.action.key;

import com.bobocode.svydovets.bibernate.util.EntityUtils;

public record EntityKey<T>(Class<T> type, Object id) {

    public static <T> EntityKey<T> of(Class<T> type, Object id) {
        return new EntityKey<>(type, id);
    }

    public static <T> EntityKey<?> valueOf(T entity) {
        var id = EntityUtils.retrieveIdValue(entity);
        return new EntityKey<>(entity.getClass(), id.orElse(null));
    }
}
