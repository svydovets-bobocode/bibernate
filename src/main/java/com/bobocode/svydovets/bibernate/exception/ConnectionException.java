package com.bobocode.svydovets.bibernate.exception;

public class ConnectionException extends RuntimeException {
    public ConnectionException(String message, Throwable e) {
        super(message, e);
    }
}
