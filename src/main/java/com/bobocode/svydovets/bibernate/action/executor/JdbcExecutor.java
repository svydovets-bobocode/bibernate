package com.bobocode.svydovets.bibernate.action.executor;

import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The JdbcExecutor class provides a static method for executing a SQL query and retrieving the result set.
 */
public class JdbcExecutor {

    /**
     * Executes the specified SQL query and returns the result set obtained.
     *
     * @param query the SQL query to execute
     * @param connection the connection to use for executing the query
     *
     * @return the result set obtained by executing the query
     * @throws ConnectionException if there is an error while executing the query
     */
    public static ResultSet executeQueryAndRetrieveResultSet(String query, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (Exception e) {
            throw new ConnectionException(
                    "Error while executing query %s and collecting the ResultSet".formatted(query), e);
        }
    }
}
