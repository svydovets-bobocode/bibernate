package com.bobocode.svydovets.bibernate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityNotFoundException extends BibernateException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
