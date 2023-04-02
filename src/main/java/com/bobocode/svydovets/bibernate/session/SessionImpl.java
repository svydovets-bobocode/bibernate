package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.state.EntityState.DETACHED;
import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;
import static com.bobocode.svydovets.bibernate.state.EntityState.REMOVED;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.getIdValue;

import com.bobocode.svydovets.bibernate.action.ActionQueue;
import com.bobocode.svydovets.bibernate.action.DeleteAction;
import com.bobocode.svydovets.bibernate.action.InsertAction;
import com.bobocode.svydovets.bibernate.action.UpdateAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.constant.ErrorMessage;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.state.EntityState;
import com.bobocode.svydovets.bibernate.state.EntityStateService;
import com.bobocode.svydovets.bibernate.state.EntityStateServiceImpl;
import com.bobocode.svydovets.bibernate.transaction.Transaction;
import com.bobocode.svydovets.bibernate.transaction.TransactionImpl;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionImpl implements Session {

    // todo: replace with Queue<Action>

    private final ActionQueue actionQueue = new ActionQueue();
    private final Connection connection;
    private final Transaction transaction;
    private final SearchService searchService;
    private final EntityStateService entityStateService;

    private final Map<EntityKey<?>, Object> entitiesCacheMap = new ConcurrentHashMap<>();
    private final Map<EntityKey<?>, Object[]> entitiesSnapshotMap = new ConcurrentHashMap<>();

    private final RequiredAnnotationValidatorProcessor validatorProcessor =
            new RequiredAnnotationValidatorProcessorImpl();

    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    public SessionImpl(Connection connection, SearchService searchService) {
        this.connection = connection;
        this.transaction = new TransactionImpl(connection);
        this.searchService = searchService;
        this.entityStateService = new EntityStateServiceImpl();
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        verifySessionIsOpened();

        EntityKey<T> entityKey = EntityKey.of(type, id);
        T entity = type.cast(entitiesCacheMap.computeIfAbsent(entityKey, this::loadEntity));

        entityStateService.setEntityState(entity, MANAGED);
        return entity;
    }

    private <T> Object loadEntity(EntityKey<?> entityKey) {
        Object loadedEntity = searchService.findOne(entityKey);
        entitiesSnapshotMap.computeIfAbsent(
                entityKey, k -> EntityUtils.getFieldValuesFromEntity(loadedEntity));
        return loadedEntity;
    }

    @Override
    public <T> T save(T entity) {
        verifySessionIsOpened();
        EntityKey<?> entityKey = EntityKey.valueOf(entity);
        actionQueue.addAction(entityKey, new InsertAction<>(this.connection, entity));
        entitiesCacheMap.put(entityKey, entity);
        entityStateService.setEntityState(entity, EntityState.MANAGED);
        return null;
    }

    @Override
    public void delete(Object object) {
        verifySessionIsOpened();
        EntityKey<?> entityKey = EntityKey.valueOf(object);

        actionQueue.addAction(entityKey, new DeleteAction<>(this.connection, object));
        entitiesCacheMap.remove(entityKey);
        entitiesSnapshotMap.remove(entityKey);
        entityStateService.setEntityState(entityKey, REMOVED);
    }

    @Override
    public <T> Collection<T> findAll(Class<T> type) {
        verifySessionIsOpened();
        flush();
        return searchService.findAllByType(type, entitiesCacheMap, entitiesSnapshotMap);
    }

    @Override
    public <T> List<T> findAll(Class<T> type, Map<String, Object> properties) {
        verifySessionIsOpened();
        // todo add set entity state when it will be implemented
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

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null.");
        }

        Class<T> entityType = (Class<T>) entity.getClass();
        validatorProcessor.validate(entityType);

        EntityKey<T> entityKey = new EntityKey<>(entityType, getIdValue(entity));
        entityStateService.validate(entityKey, MANAGED);

        T managedEntity = entityType.cast(entitiesCacheMap.get(entityKey));

        // If the entity is not in the cache, retrieve it from the database
        if (managedEntity == null) {
            managedEntity = searchService.findOne(entityKey);
        }

        // Merge the states of the detached and managed entities
        for (Field field : entityType.getDeclaredFields()) {
            EntityUtils.updateManagedEntityField(entity, managedEntity, field);
        }

        // Add the merged entity to the cache
        entitiesCacheMap.put(entityKey, managedEntity);
        entityStateService.setEntityState(entityKey, MANAGED);

        UpdateAction<T> updateAction = new UpdateAction<>(connection, managedEntity);
        actionQueue.addAction(entityKey, updateAction);

        return managedEntity;
    }

    @Override
    public void detach(Object entity) {
        verifySessionIsOpened();
        EntityKey<?> entityKey = EntityKey.valueOf(entity);
        entitiesCacheMap.remove(entityKey);
        entitiesSnapshotMap.remove(entityKey);
        entityStateService.setEntityState(entity, DETACHED);
    }

    @Override
    public void flush() {
        verifySessionIsOpened();
        actionQueue.executeAll();
        actionQueue.clear();
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
        entityStateService.clearState();
        transaction.commit();
    }

    @Override
    public void rollback() {
        // TODO: think about do we need to clear the persistence context maps
        verifySessionIsOpened();
        entityStateService.clearState();
        transaction.rollback();
    }

    private void verifySessionIsOpened() {
        if (!isOpen.get()) {
            throw new BibernateException(ErrorMessage.SESSION_IS_CLOSED);
        }
    }

    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public EntityState getEntityState(Object entity) {
        return entityStateService.getEntityState(entity);
    }
}
