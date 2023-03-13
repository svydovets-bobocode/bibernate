package com.bobocode.svydovets.bibernate.session;

import java.util.List;
import java.util.Map;

public interface Session {

    <T> T find(Class<T> type, Object id);

    <T> T save(T entity);

    <T> void delete(T id);

    <T> List<T> findAll(Class<T> type);

    <T> List<T> findAll(Class<T> type, Map<String, Object> properties);

    void close();

    <T> T merge(T entity);

    void detach(Object entity);

    void flush();

}
