package com.bobocode.svydovets.bibernate.action.executor;

import com.bobocode.svydovets.bibernate.exception.ConnectionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.sql.DataSource;

public class JdbcExecutor {
    public static <T> T executeQuery(
            DataSource dataSource,
            String query,
            Consumer<PreparedStatement> statementConsumer,
            Function<ResultSet, T> resultSetMapper) {

        try (var connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statementConsumer.accept(statement);
            ResultSet resultSet = statement.executeQuery();

            return resultSetMapper.apply(resultSet);
        } catch (SQLException e) {
            throw new ConnectionException("Unable to connect to datasource", e);
        }
    }

    public static ResultSet executeQueryAndRetrieveResultSet(String query, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            return statement.executeQuery();
        } catch (Exception e) {
            throw new ConnectionException(
                    "Error while executing query %s and collecting the ResultSet".formatted(query), e);
        }
    }
}
