package com.bobocode.svydovets.bibernate.action;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Tag("integration")
abstract class AbstractIntegrationTest {
    protected JdbcDataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        connection = dataSource.getConnection();
        createTable();
        insertIntoTable();
    }

    @AfterEach
    void tearDown() throws SQLException {
        dropTable();
        connection.close();
    }

    private void createTable() throws SQLException {
        String createTableQuery = "CREATE TABLE person (id BIGINT, firstName VARCHAR(255), lastName VARCHAR(255))";
        PreparedStatement statement = connection.prepareStatement(createTableQuery);
        statement.executeUpdate();
    }

    private void insertIntoTable() throws SQLException {
        String insertQuery = "INSERT INTO person VALUES (1, 'John', 'Doe')";
        PreparedStatement statement = connection.prepareStatement(insertQuery);
        statement.executeUpdate();
    }

    private void dropTable() throws SQLException {
        String dropTableQuery = "DROP TABLE person";
        PreparedStatement statement = connection.prepareStatement(dropTableQuery);
        statement.executeUpdate();
    }
}
