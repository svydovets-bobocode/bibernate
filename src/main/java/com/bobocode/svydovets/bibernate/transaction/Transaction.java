package com.bobocode.svydovets.bibernate.transaction;

public interface Transaction {
    void begin();

    void commit();

    void rollback();
}
