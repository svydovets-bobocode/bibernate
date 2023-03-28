package com.bobocode.svydovets.bibernate.session;

import java.util.List;
import java.util.Map;

public interface Session {

    <T> T find(Class<T> type, Object id);

    <T> T save(T entity);

    void delete(Object object);

    <T> List<T> findAll(Class<T> type);

    <T> List<T> findAll(Class<T> type, Map<String, Object> properties);

    void close();

    /**
     * Merges the state of the given entity with the current state of a managed entity in the
     * persistence context. If the given entity is transient, it will be saved as a new entity. If the
     * given entity is detached, the state of the entity will be updated to match the given entity.
     *
     * @param <T> The type of the entity being merged.
     * @param entity The entity to be merged. Must not be {@code null}.
     * @return The managed entity with its state updated to match the given entity.
     * @throws IllegalArgumentException If the given entity is {@code null}.
     * @throws IllegalStateException If the session is not opened or the entity class validation
     *     fails.
     */
    <T> T merge(T entity);

    void detach(Object entity);

    void flush();

    void begin();

    void commit();

    void rollback();
}
