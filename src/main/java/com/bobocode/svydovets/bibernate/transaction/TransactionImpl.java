package com.bobocode.svydovets.bibernate.transaction;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_WHILE_BEGINNING_TRANSACTION;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_WHILE_COMMITTING_TRANSACTION;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.ERROR_WHILE_ROLLING_BACK_TRANSACTION;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.TRANSACTION_IS_ALREADY_STARTED;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.TRANSACTION_NOT_STARTED_OR_ALREADY_COMMITTED;

import com.bobocode.svydovets.bibernate.exception.TransactionException;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionImpl implements Transaction {
    private final Connection connection;
    private boolean isStarted = false;

    public TransactionImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void begin() {
        if (isStarted) {
            throw new TransactionException(TRANSACTION_IS_ALREADY_STARTED);
        }
        log.info("Begin transaction");
        try {
            connection.setAutoCommit(false);
            isStarted = true;
        } catch (SQLException e) {
            throw new TransactionException(ERROR_WHILE_BEGINNING_TRANSACTION, e);
        }
    }

    @Override
    public void commit() {
        if (!isStarted) {
            throw new TransactionException(TRANSACTION_NOT_STARTED_OR_ALREADY_COMMITTED);
        }
        log.info("Commit transaction");
        try {
            connection.commit();
            isStarted = false;
        } catch (SQLException e) {
            throw new TransactionException(ERROR_WHILE_COMMITTING_TRANSACTION, e);
        }
    }

    @Override
    public void rollback() {
        if (!isStarted) {
            throw new TransactionException(TRANSACTION_NOT_STARTED_OR_ALREADY_COMMITTED);
        }
        log.info("Rollback transaction");
        try {
            connection.rollback();
            isStarted = false;
        } catch (SQLException e) {
            throw new TransactionException(ERROR_WHILE_ROLLING_BACK_TRANSACTION, e);
        }
    }
}
