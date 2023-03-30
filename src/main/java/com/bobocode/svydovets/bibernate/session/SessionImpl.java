package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.state.EntityState.DETACHED;
import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;
import static com.bobocode.svydovets.bibernate.state.EntityState.REMOVED;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.getIdValue;

import com.bobocode.svydovets.bibernate.action.DeleteAction;
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
    private final DeleteAction deleteAction;
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
        this.deleteAction = new DeleteAction(this.connection);
        // todo: change it to single constructor call
        this.searchService = searchService;
        this.searchService.setEntitiesMaps(entitiesCacheMap, entitiesSnapshotMap);
        this.entityStateService = new EntityStateServiceImpl();
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        verifySessionIsOpened();

        EntityKey<T> key = EntityKey.of(type, id);
        T entity = searchService.findOne(key);

        entityStateService.setEntityState(entity, MANAGED);
        return entity;
    }

    @Override
    public <T> T save(T entity) {
        verifySessionIsOpened();
        entityStateService.setEntityState(entity, MANAGED);
        return null;
    }

    @Override
    public void delete(Object object) {
        verifySessionIsOpened();
        EntityKey<?> entityKey = EntityKey.valueOf(object);

        // Todo: push it to Query action
        deleteAction.execute(entityKey);

        entitiesCacheMap.remove(entityKey);
        entitiesSnapshotMap.remove(entityKey);
        entityStateService.setEntityState(entityKey, REMOVED);
    }

    @Override
    public <T> Collection<T> findAll(Class<T> type) {
        verifySessionIsOpened();

        flush();

        Map<EntityKey<T>, T> entityMap = searchService.findAllByType(type);
        entityMap.forEach(
                (key, value) -> {
                    entitiesCacheMap.put(key, value);
                    entitiesSnapshotMap.put(key, EntityUtils.getFieldValuesFromEntity(value));
                    entityStateService.setEntityState(key, MANAGED);
                });

        return entityMap.values();
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
