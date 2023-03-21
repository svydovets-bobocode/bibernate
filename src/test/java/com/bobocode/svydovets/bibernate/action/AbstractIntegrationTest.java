package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import com.bobocode.svydovets.bibernate.config.PropertyFileConfiguration;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("integration")
public abstract class AbstractIntegrationTest {
    protected static DataSource dataSource;
    protected Connection connection;

    @BeforeAll
    static void beforeAll() {
        ConfigurationSource source =
                new PropertyFileConfiguration("test_svydovets_bibernate.properties");
        dataSource = new HikariConnectionPool().getDataSource(source);
    }

    @BeforeEach
    void setUp() throws SQLException {
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
        String createTableQuery =
                "CREATE TABLE person (id BIGINT, first_name VARCHAR(255), last_name VARCHAR(255))";
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
