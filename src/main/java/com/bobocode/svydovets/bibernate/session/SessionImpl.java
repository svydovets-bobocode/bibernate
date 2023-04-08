package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.state.EntityState.DETACHED;
import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;
import static com.bobocode.svydovets.bibernate.state.EntityState.REMOVED;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.convertFieldValuesMapToSnapshotArray;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.getFieldValuesFromEntity;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.getIdValue;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.getSnapshotArrayForEntity;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.updateManagedEntityField;

import com.bobocode.svydovets.bibernate.action.ActionQueue;
import com.bobocode.svydovets.bibernate.action.DeleteAction;
import com.bobocode.svydovets.bibernate.action.InsertAction;
import com.bobocode.svydovets.bibernate.action.UpdateAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.constant.ErrorMessage;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.locking.optimistic.OptimisticLockService;
import com.bobocode.svydovets.bibernate.session.service.IdResolverService;
import com.bobocode.svydovets.bibernate.session.service.SearchService;
import com.bobocode.svydovets.bibernate.session.service.model.SearchMetadata;
import com.bobocode.svydovets.bibernate.state.EntityState;
import com.bobocode.svydovets.bibernate.state.EntityStateService;
import com.bobocode.svydovets.bibernate.state.EntityStateServiceImpl;
import com.bobocode.svydovets.bibernate.transaction.Transaction;
import com.bobocode.svydovets.bibernate.transaction.TransactionImpl;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessor;
import com.bobocode.svydovets.bibernate.validation.annotation.required.processor.RequiredAnnotationValidatorProcessorImpl;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionImpl implements Session {

    private final ActionQueue actionQueue;
    private final Connection connection;
    private final Transaction transaction;
    private final SearchService searchService;
    private final IdResolverService idService;
    private final EntityStateService entityStateService;
    private final OptimisticLockService optimisticLockService;

    private final Map<EntityKey<?>, Object> entitiesCacheMap = new ConcurrentHashMap<>();
    private final Map<EntityKey<?>, Map<String, Object>> entitiesSnapshotMap =
            new ConcurrentHashMap<>();

    private final RequiredAnnotationValidatorProcessor validatorProcessor =
            new RequiredAnnotationValidatorProcessorImpl();

    private final AtomicBoolean isOpen = new AtomicBoolean(true);

    public SessionImpl(Connection connection, SearchService searchService) {
        this.connection = connection;
        this.searchService = searchService;
        this.transaction = new TransactionImpl(connection);
        this.idService = new IdResolverService();
        this.entityStateService = EntityStateServiceImpl.getInstance();
        this.actionQueue = new ActionQueue();
        this.searchService.setResultSetMapper(new ResultSetMapper(this));
        this.optimisticLockService = new OptimisticLockService();
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        return find(type, id, LockModeType.NONE);
    }

    @Override
    public <T> T find(Class<T> type, Object id, LockModeType lockModeType) {
        verifySessionIsOpened();

        EntityKey<T> entityKey = EntityKey.of(type, id);
        T entity =
                type.cast(
                        entitiesCacheMap.computeIfAbsent(entityKey, key -> loadEntity(key, lockModeType)));

        entityStateService.setEntityState(entity, MANAGED);
        return entity;
    }

    private Object loadEntity(EntityKey<?> entityKey, LockModeType lockModeType) {
        Object loadedEntity = searchService.findOne(entityKey, lockModeType);
        entitiesSnapshotMap.computeIfAbsent(entityKey, k -> getFieldValuesFromEntity(loadedEntity));
        return loadedEntity;
    }

    @Override
    public <T> T save(T entity) {
        verifySessionIsOpened();
        idService.resolveIdValue(this.connection, entity);

        EntityKey<?> entityKey = EntityKey.valueOf(entity);

        optimisticLockService.syncVersionValueWithSnapshotIfNeeds(
                entity, entityKey, entitiesSnapshotMap);

        actionQueue.addAction(entityKey, new InsertAction<>(this.connection, entity));
        entitiesCacheMap.put(entityKey, entity);
        entitiesSnapshotMap.put(entityKey, getFieldValuesFromEntity(entity));
        entityStateService.setEntityState(entity, EntityState.MANAGED);
        return entity;
    }

    @Override
    public void delete(Object object) {
        verifySessionIsOpened();
        EntityKey<?> entityKey = EntityKey.valueOf(object);

        optimisticLockService.syncVersionValueWithSnapshotIfNeeds(
                object, entityKey, entitiesSnapshotMap);

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
    public <T> List<T> findAllBy(Class<T> entityType, Field field, Object columnValue) {
        SearchMetadata<T> searchMetadata = new SearchMetadata<>(entityType, field, columnValue);
        return searchService.findAllBy(searchMetadata, entitiesCacheMap, entitiesSnapshotMap);
    }

    @Override
    public void close() {
        try {
            flush();
            detachAllManagedEntities();
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
        Preconditions.checkArgument(entity != null, "Entity cannot be null.");

        Class<T> entityType = (Class<T>) entity.getClass();
        validatorProcessor.validate(entityType);

        EntityKey<T> entityKey = new EntityKey<>(entityType, getIdValue(entity));
        entityStateService.validate(entityKey, DETACHED, MANAGED);

        T managedEntity = entityType.cast(entitiesCacheMap.get(entityKey));

        if (managedEntity == null) {
            managedEntity = searchService.findOne(entityKey);
            entitiesSnapshotMap.put(entityKey, getFieldValuesFromEntity(managedEntity));
        }

        for (Field field : entityType.getDeclaredFields()) {
            updateManagedEntityField(entity, managedEntity, field);
        }

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
        performDirtyChecking();
        actionQueue.executeAllWithOrder();
        actionQueue.clear();
    }

    @Override
    public void beginTransaction() {
        verifySessionIsOpened();
        transaction.begin();
    }

    @Override
    public void commitTransaction() {
        verifySessionIsOpened();
        flush();
        transaction.commit();
    }

    @Override
    public void rollbackTransaction() {
        verifySessionIsOpened();
        entityStateService.clearState();
        transaction.rollback();
    }

    private void detachAllManagedEntities() {
        entitiesCacheMap.values().stream()
                .filter(entity -> entityStateService.getEntityState(entity).equals(MANAGED))
                .forEach(entity -> entityStateService.setEntityState(entity, DETACHED));
    }

    public void verifySessionIsOpened() {
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

    private void performDirtyChecking() {
        log.trace("Starting dirty checking...");
        for (EntityKey<?> entityKey : entitiesCacheMap.keySet()) {
            Object currentEntity = entitiesCacheMap.get(entityKey);
            Object[] currentEntitySnapshot = getSnapshotArrayForEntity(currentEntity);

            Map<String, Object> initialFieldValuesMap = entitiesSnapshotMap.get(entityKey);
            Object[] initialEntitySnapshot = convertFieldValuesMapToSnapshotArray(initialFieldValuesMap);

            log.debug("Comparing snapshots: {} | {}", initialEntitySnapshot, currentEntitySnapshot);
            if (!Arrays.equals(currentEntitySnapshot, initialEntitySnapshot)) {
                log.trace("Snapshots are not equal, found dirty entity {}", currentEntity);
                log.trace("Creating the update action...");
                UpdateAction<?> updateAction = new UpdateAction<>(connection, currentEntity);
                actionQueue.addAction(entityKey, updateAction);
            }
        }
    }
}
