package com.bobocode.svydovets.bibernate.session.service;

import com.bobocode.svydovets.bibernate.constant.GenerationType;
import java.sql.Connection;

public interface IdValuePopulator {
    <T> void populateIdValue(Connection connection, T entity);

    GenerationType getType();
}
