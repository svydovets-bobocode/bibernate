package com.bobocode.svydovets.bibernate.exception;

public class BibernateException extends RuntimeException {
    public BibernateException(String message) {
        super(message);
    }

    public BibernateException(Throwable cause) {
        super(cause);
    }

    public BibernateException(String message, Throwable cause) {
        super(message, cause);
    }
}
