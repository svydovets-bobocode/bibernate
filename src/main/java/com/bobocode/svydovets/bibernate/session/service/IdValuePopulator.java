package com.bobocode.svydovets.bibernate.session.service;

import com.bobocode.svydovets.bibernate.constant.GenerationType;
import java.sql.Connection;

/** IdValuePopulator provides generation functionality of ID values for entities. */
public interface IdValuePopulator {

    /**
     * Get id value based on the id generation strategy and populate it into entity
     *
     * @param connection - connection to the data source
     * @param entity - entity before putting into persistent context
     * @param <T> - type of entity
     */
    <T> void populateIdValue(Connection connection, T entity);

    /**
     * Method represents what type of id generation strategy represents IdValuePopulator
     *
     * @return type of generation strategy
     */
    GenerationType getType();
}
