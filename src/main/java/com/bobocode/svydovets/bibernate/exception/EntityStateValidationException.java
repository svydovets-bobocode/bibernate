package com.bobocode.svydovets.bibernate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityStateValidationException extends BibernateException {
    public EntityStateValidationException(String message) {
        super(message);
        log.error(message);
    }
}
