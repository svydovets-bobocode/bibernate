package com.bobocode.svydovets.bibernate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigurationException extends BibernateException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
