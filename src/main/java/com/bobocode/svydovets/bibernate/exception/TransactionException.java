package com.bobocode.svydovets.bibernate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionException extends BibernateException {
    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
