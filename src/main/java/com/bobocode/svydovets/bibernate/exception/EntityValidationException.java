package com.bobocode.svydovets.bibernate.exception;

public class EntityValidationException extends BibernateException {
    public EntityValidationException(String message) {
        super(message);
    }
}
