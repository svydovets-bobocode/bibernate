package com.bobocode.svydovets.bibernate.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the Pessimistic Concurrency Control (PCC) locking strategies. Currently is supported
 * only for the {@link Session#find(Class, Object, LockModeType)}. When Bibernate retrieves the
 * entity - it uses one of the available lock types.
 *
 * <p>The lock will be obtained till the transaction commit or rollback.
 *
 * @see Session#find(Class, Object, LockModeType)
 * @see Session#commitTransaction()
 * @see Session#rollbackTransaction()
 */
@AllArgsConstructor
public enum LockModeType {
    /** Use this lock type to retrieve the entity without no locking. */
    NONE(""),

    /** Represents the READ-LOCK. */
    FOR_SHARE("FOR SHARE"),

    /** Represent the WRITE-LOCK. */
    FOR_UPDATE("FOR UPDATE");

    @Getter private final String value;
}
