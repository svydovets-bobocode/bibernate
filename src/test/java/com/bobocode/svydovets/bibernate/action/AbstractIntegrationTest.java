package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import com.bobocode.svydovets.bibernate.config.PropertyFileConfiguration;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.session.SearchService;
import com.bobocode.svydovets.bibernate.session.SessionImpl;
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
    protected SearchService searchService;

    @BeforeAll
    static void beforeAll() {
        // to switch between DBs, use different property files FE
        // bibernate.properties
        ConfigurationSource source =
                new PropertyFileConfiguration("test_svydovets_bibernate_h2.properties");
        dataSource = new HikariConnectionPool().getDataSource(source);
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection = dataSource.getConnection();
        searchService = new SearchService(connection);
        searchService.setResultSetMapper(
                new ResultSetMapper(new SessionImpl(connection, searchService)));
        createTable();
        insertIntoTable();
    }

    @AfterEach
    void tearDown() throws SQLException {
        dropTable();
        connection.close();
    }

    // Todo: move that to schema or different place
    private void createTable() throws SQLException {
        String createTableQuery =
                "CREATE TABLE person (id BIGINT, first_name VARCHAR(255), last_name VARCHAR(255))";
        PreparedStatement statement = connection.prepareStatement(createTableQuery);
        statement.executeUpdate();
    }

    private void insertIntoTable() throws SQLException {
        String insertQuery = "INSERT INTO person VALUES (1, 'John', 'Doe'), (2, 'Martin', 'Fowler')";
        PreparedStatement statement = connection.prepareStatement(insertQuery);
        statement.executeUpdate();
    }

    private void dropTable() throws SQLException {
        String dropTableQuery = "DROP TABLE IF EXISTS %s";
        String[] tables = {"person", "users"};
        for (var table : tables) {
            PreparedStatement statement = connection.prepareStatement(dropTableQuery.formatted(table));
            statement.executeUpdate();
        }
    }
}
