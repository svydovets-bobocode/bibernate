package com.bobocode.svydovets.bibernate.transaction;

import com.bobocode.svydovets.bibernate.exception.TransactionException;
import com.bobocode.svydovets.bibernate.session.Session;

/**
 * Interface used to control transaction per {@link Session}. The {@link Session} uses the
 * associated {@link Transaction} object.
 */
public interface Transaction {
    /**
     * Start a transaction.
     *
     * @throws TransactionException if {@code isStarted} is true
     */
    void begin();

    /**
     * Commit the current transaction, writing any unflushed changes to the database.
     *
     * @throws TransactionException if {@code isStarted} is false
     * @throws TransactionException if the commit fails
     */
    void commit();

    /**
     * Roll back the current transaction.
     *
     * @throws TransactionException if {@code isStarted} is false
     * @throws TransactionException if an unexpected error condition is encountered
     */
    void rollback();
}
