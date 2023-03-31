package com.bobocode.svydovets.bibernate;

import com.bobocode.svydovets.bibernate.action.DeleteAction;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import com.bobocode.svydovets.bibernate.config.PropertyFileConfiguration;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.session.SearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

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
        createTables();
        insertIntoTables();
    }

    @AfterEach
    void tearDown() throws SQLException {
        dropTables();
        connection.close();
    }

    // Todo: move that to schema or different place
    private void createTables() throws SQLException {
        createPersonTable();
        createUsersTable();
    }

    private void insertIntoTables() throws SQLException {
        String insertPersonsQuery =
                "INSERT INTO person(id, first_name, last_name) VALUES (1, 'John', 'Doe'), (2, 'Martin', 'Fowler')";
        connection.prepareStatement(insertPersonsQuery).execute();

        String insertUsersQuery = "INSERT INTO users (name) VALUES ('Test')";
        connection.prepareStatement(insertUsersQuery).execute();
    }

    private void createUsersTable() throws SQLException {
        String createTableSql =
                """
                        CREATE TABLE IF NOT EXISTS users
                        (
                            id           INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            name         VARCHAR,
                            creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                            phone_number VARCHAR
                        );
                        """;

        connection.prepareStatement(createTableSql).executeUpdate();
    }


    private void createPersonTable() throws SQLException {
        String createTableQuery =
                """
                        CREATE TABLE person 
                        (
                            id       BIGINT ,
                            first_name VARCHAR(255),
                            last_name  VARCHAR(255)
                        );
                         """;

        PreparedStatement statement = connection.prepareStatement(createTableQuery);
        statement.executeUpdate();
    }

    private void dropTables() throws SQLException {
        String dropTablePerson = "DROP TABLE person";
        String dropTableUsers = "DROP TABLE users";
        connection.prepareStatement(dropTablePerson).executeUpdate();
        connection.prepareStatement(dropTableUsers).executeUpdate();
    }
}
