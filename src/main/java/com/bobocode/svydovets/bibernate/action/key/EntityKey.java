package com.bobocode.svydovets.bibernate.action.key;

import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.session.SessionImpl;
import com.bobocode.svydovets.bibernate.util.EntityUtils;

/**
 * Entity key is used to determine a uniqueness of entities in {@link Session}. Used as a key in
 * {@link SessionImpl#entitiesSnapshotMap} and {@link SessionImpl#entitiesCacheMap}
 *
 * @param type entity class
 * @param id primary key
 */
public record EntityKey<T>(Class<T> type, Object id) {

    public static <T> EntityKey<T> of(Class<T> type, Object id) {
        return new EntityKey<>(type, id);
    }

    /**
     * Returns an EntityKey instance for the specified entity object.
     *
     * @param entity the entity object for which to retrieve an EntityKey
     * @return an EntityKey instance for the specified entity object
     */
    public static <T> EntityKey<?> valueOf(T entity) {
        var id = EntityUtils.retrieveIdValue(entity);
        return new EntityKey<>(entity.getClass(), id.orElse(null));
    }
}
