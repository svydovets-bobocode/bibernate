package com.bobocode.svydovets.bibernate.action.executor;

import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import lombok.extern.slf4j.Slf4j;

/** JdbcExecutor provides static methods to execute SQL queries and statements using JDBC. */
@Slf4j
public class JdbcExecutor {

    /**
     * Executes a SQL query and returns the result set.
     *
     * @param query the SQL query to execute
     * @param connection the connection to use for executing the query
     * @return the result set obtained by executing the query
     * @throws ConnectionException if there is an error while executing the query
     */
    public static ResultSet executeQueryAndRetrieveResultSet(String query, Connection connection) {
        try {
            return executePreparedStatementAndRetrieveResultSet(connection.prepareStatement(query));
        } catch (Exception e) {
            throw new ConnectionException(
                    "Error while executing query %s and collecting the ResultSet".formatted(query), e);
        }
    }

    /**
     * Executes a prepared statement and returns the result set.
     *
     * @param preparedStatement the prepared statement to execute
     * @return the result set obtained by executing the prepared statement
     * @throws ConnectionException if there is an error while executing the prepared statement
     */
    public static ResultSet executePreparedStatementAndRetrieveResultSet(
            PreparedStatement preparedStatement) {
        try {
            var resultSet = preparedStatement.executeQuery();
            log.debug("Executed statement: ".concat(getFormattedPreparedStatement(preparedStatement)));
            return resultSet;
        } catch (Exception e) {
            throw new ConnectionException(
                    "Error while executing statement %s and collecting the ResultSet"
                            .formatted(preparedStatement),
                    e);
        }
    }

    /**
     * Executes a prepared statement.
     *
     * @param preparedStatement the prepared statement to execute
     * @throws ConnectionException if there is an error while executing the prepared statement
     */
    public static void executePreparedStatement(PreparedStatement preparedStatement) {
        try {
            preparedStatement.execute();
            log.debug("Executed statement: ".concat(getFormattedPreparedStatement(preparedStatement)));
        } catch (Exception e) {
            throw new ConnectionException(
                    "Error while executing statement %s".formatted(preparedStatement), e);
        }
    }

    /**
     * Executes a prepared statement update and returns the number of affected rows.
     *
     * @param preparedStatement the prepared statement to execute
     * @return the number of rows affected by the update
     * @throws ConnectionException if there is an error while executing the prepared statement
     */
    public static int executePreparedStatementUpdate(PreparedStatement preparedStatement) {
        try {
            int rows = preparedStatement.executeUpdate();
            log.debug(
                    "Executed statement: "
                            .concat(
                                    getFormattedPreparedStatement(preparedStatement)
                                            .concat("\nUpdated %s rows".formatted(rows))));
            return rows;
        } catch (Exception e) {
            throw new ConnectionException(
                    "Error while executing statement %s".formatted(preparedStatement), e);
        }
    }

    /**
     * Returns a formatted string representation of a prepared statement.
     *
     * @param preparedStatement the prepared statement to format
     * @return a formatted string representation of the prepared statement
     */
    private static String getFormattedPreparedStatement(PreparedStatement preparedStatement) {
        String[] parts = preparedStatement.toString().split(":", 2);
        if (parts.length > 1) {
            return parts[1].trim();
        } else {
            return preparedStatement.toString();
        }
    }
}
