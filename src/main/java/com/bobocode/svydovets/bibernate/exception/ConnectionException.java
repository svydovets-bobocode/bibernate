package com.bobocode.svydovets.bibernate.exception;

public class ConnectionException extends BibernateException {
    public ConnectionException(String message, Throwable e) {
        super(message, e);
    }
}
