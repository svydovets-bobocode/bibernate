package com.bobocode.svydovets.bibernate.action.executor;

import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JdbcExecutor {

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
