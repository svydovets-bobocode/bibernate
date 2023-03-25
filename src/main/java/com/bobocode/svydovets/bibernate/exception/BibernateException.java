package com.bobocode.svydovets.bibernate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BibernateException extends RuntimeException {
    public BibernateException(String message) {
        super(message);
        log.error(message);
    }

    public BibernateException(Throwable cause) {
        super(cause);
    }

    public BibernateException(String message, Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }
}
