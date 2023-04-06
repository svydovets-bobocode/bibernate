package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.ActionQueue;
import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.exception.EntityNotFoundException;
import com.bobocode.svydovets.bibernate.state.EntityState;
import com.bobocode.svydovets.bibernate.transaction.Transaction;
import com.bobocode.svydovets.bibernate.validation.Validator;
import com.bobocode.svydovets.bibernate.validation.state.EntityStateTransition;
import java.util.Collection;

/**
 * The main abstraction, which represents the power of the ORM. Provides an API to perform basic DB
 * operations.
 *
 * <p>All the methods will only with the correctly mapped Entity. The minimum requirements for the
 * Entity is the presence of the {@link Entity} class-level annotation and {@link Id} field-level
 * annotation.
 *
 * <p>If you will pass the non-mapped entity or incorrectly mapped entity: your Session will fail
 * and you will face the corresponding exception.
 *
 * <p>When you work with entities using Session, your entities may transit from one logical {@link
 * EntityState} to another.
 *
 * <p>Session provides you with the first-level cache. When entity is retrieved from the DB for the
 * first time, it is cached. Also, the Session stores the snapshot object arrays for all the
 * entities, in order to be able to trigger the dirty checking on Session flushing.
 *
 * <p>When you perform the operation on your Entity - they are not sent to the DB at the time.
 * Instead, they are added to the {@link ActionQueue}, which postpones the DB operations, optimizes
 * them and provide you with the next features: 1. Queries mey be not executed at all, if you
 * created an entity and deleted it within one TX. 2. All the operations will be sent only by one
 * DB-request, which decreases the latency and optimizes the perfomance.
 *
 * <p>You have to close your Session as soon as possible. When Session is opened - it stores in
 * memory all the cached entities and it's snapshots. Also, if you was using {@link LockModeType},
 * the corresponding rows in DB will be locked until you either close your transaction or Session.
 * Also, the connection to the DB is linked with the Session and will be returned to the Connection
 * Poll on {@link Session#close()}.
 *
 * @see Entity
 * @see Id
 * @see Validator
 * @see EntityState
 * @see EntityStateTransition
 * @see LockModeType
 * @see ActionQueue
 */
public interface Session {

    /**
     * Find by primary key. Search for an entity of the specified class and primary key. If the entity
     * instance is contained in the cache, it is returned from there.
     *
     * @param type entity class
     * @param id primary key
     * @return the found entity instance
     * @throws EntityNotFoundException if entity is not exists in the DB
     */
    <T> T find(Class<T> type, Object id);

    /**
     * Find by primary key. Search for an entity of the specified class and primary key. If the entity
     * instance is contained in the cache, it is returned from there. Here you are able to provide
     * {@link LockModeType}, which will be used for the pessimistic concurrency control on the row
     * level.
     *
     * <p>You will obtain the lock on the entity and it will be possible to perform changes on this
     * entity only in your current transaction. The lock will be released on the transaction {@link
     * Session#commit()} or {@link Session#rollback()}.
     *
     * @param type entity class
     * @param id primary key
     * @param lockModeType type of the pessimistic locking strategy
     * @return the found entity instance
     * @throws EntityNotFoundException if entity is not exists in the DB
     * @see LockModeType
     */
    <T> T find(Class<T> type, Object id, LockModeType lockModeType);

    /**
     * Make an instance managed and persistent. This operation changes the entity's status to {@link
     * EntityState#MANAGED}.
     *
     * @param entity a transient instance of a persistent class
     * @return the generated identifier
     */
    <T> T save(T entity);

    /**
     * Remove a persistent instance from the datastore. The argument may be an instance associated
     * with the receiving {@link Session} or a transient instance with an identifier associated with
     * existing persistent state. This operation changes the entity's status to {@link
     * EntityState#REMOVED}.
     *
     * @param object the instance to be removed
     */
    void delete(Object object);

    /**
     * Find all instances by type. Search for an entity only by specified class. If the entity
     * instance is contained in the cache, it is returned from there.
     *
     * @param type entity class
     * @return the found entity instances or empty collection if the entity does not exist
     */
    <T> Collection<T> findAll(Class<T> type);

    /**
     * End the session by releasing the JDBC connection and cleaning up. Under the hood calls a {@link
     * Session#flush()} and clear cache.
     *
     * @throws BibernateException If indicates problems cleaning up.
     * @throws BibernateException If after session closed and when any method is called.
     */
    void close();

    /**
     * Merges the state of the given entity with the current state of a managed entity in the
     * persistence context. If the given entity is detached, the state of the entity will be updated
     * to match the given entity.
     *
     * @param <T> The type of the entity being merged.
     * @param entity The entity to be merged. Must not be {@code null}.
     * @return The managed entity with its state updated to match the given entity.
     * @throws IllegalArgumentException If the given entity is {@code null}.
     * @throws IllegalStateException If the session is not opened or the entity class validation
     *     fails.
     */
    <T> T merge(T entity);

    /**
     * Remove this instance from the session cache. Changes to the instance will not be synchronized
     * with the database. This operation changes the entity's status to {@link EntityState#DETACHED}.
     *
     * @param entity the managed instance to detach
     */
    void detach(Object entity);

    /**
     * Force this session to flush. Must be called at the end of a unit of work, before the
     * transaction is committed. Depending on the current, the session automatically flush when {@link
     * Transaction#commit()} is called, and it is not necessary to call this method directly.
     *
     * <p><em>Flushing</em> is the process of synchronizing the underlying persistent store with
     * persistable state held in memory.
     *
     * @throws BibernateException if changes could not be synchronized with the database
     */
    void flush();

    /** Begin a new transaction. Using {@link Transaction#begin()} */
    void begin();

    /**
     * Commit the current transaction, making any changes to the database permanent. Using {@link
     * Transaction#commit()}
     */
    void commit();

    /**
     * Roll back the current transaction, discarding any changes made since the transaction began.
     * Using {@link Transaction#rollback()}
     */
    void rollback();

    /**
     * Check if the session is still open.
     *
     * @return boolean
     */
    boolean isOpen();

    EntityState getEntityState(Object entity);
}
