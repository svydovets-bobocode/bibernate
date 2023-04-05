package com.bobocode.svydovets.bibernate;

import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import com.bobocode.svydovets.bibernate.config.PropertyFileConfiguration;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.session.SessionImpl;
import com.bobocode.svydovets.bibernate.session.service.IdResolverService;
import com.bobocode.svydovets.bibernate.session.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Tag("integration")
@Slf4j
public abstract class AbstractIntegrationTest {
    protected static DataSource dataSource;
    protected Connection connection;
    protected SearchService searchService;
    protected IdResolverService idResolverService;

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
        idResolverService = new IdResolverService();
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
        createCustomerTable();
        createEmployeeTable();
        createParentTable();
        createChildTable();
    }

    private void insertIntoTables() throws SQLException {
        String insertPersonsQuery =
                "INSERT INTO person(id, first_name, last_name) VALUES (1, 'John', 'Doe'), (2, 'Martin', 'Fowler')";
        connection.prepareStatement(insertPersonsQuery).execute();

        String insertUsersQuery = "INSERT INTO users (name, phone_number) VALUES ('Test', '123-123-123')";
        connection.prepareStatement(insertUsersQuery).execute();

        String insertParentQuery = "INSERT INTO parent (id, name) VALUES (4, 'Test')";
        PreparedStatement insertParentStatement = connection.prepareStatement(insertParentQuery, Statement.RETURN_GENERATED_KEYS);
        insertParentStatement.execute();

        ResultSet generatedKeys = insertParentStatement.getGeneratedKeys();
        Long parentId = 0L;
        if (generatedKeys.next()) {
            parentId = generatedKeys.getObject("id", Long.class);
        }

        String insertChildrenQuery = "INSERT INTO child (id, name, parent_id) VALUES (5, 'Test1', ?), (6, 'Test2', ?)";
        PreparedStatement insertChildStatement = connection.prepareStatement(insertChildrenQuery);
        insertChildStatement.setLong(1, parentId);
        insertChildStatement.setLong(2, parentId);
        insertChildStatement.execute();
    }

    private void createCustomerTable() throws SQLException {
        String createTableSql =
                """
                        CREATE TABLE IF NOT EXISTS customers
                        (
                            id           BIGINT PRIMARY KEY,
                            name         VARCHAR,
                            creationTime TIMESTAMP DEFAULT now(),
                            email VARCHAR
                        );
                        """;
        String createDefaultSequenceSql =
                """
                        CREATE SEQUENCE IF NOT EXISTS "customers_seq"
                        START WITH 1 INCREMENT BY 50
                        """;

        connection.prepareStatement(createTableSql).executeUpdate();
        connection.prepareStatement(createDefaultSequenceSql).executeUpdate();
    }

    private void createEmployeeTable() throws SQLException {
        String createTableSql =
                """
                        CREATE TABLE IF NOT EXISTS employees
                        (
                            id           BIGINT PRIMARY KEY,
                            name         VARCHAR,
                            creationTime TIMESTAMP DEFAULT now(),
                            email VARCHAR
                        );
                        """;
        String createCustomSequenceSql =
                """
                        CREATE SEQUENCE IF NOT EXISTS "custom_seq"
                        START WITH 1 INCREMENT BY 50
                        """;

        connection.prepareStatement(createTableSql).executeUpdate();
        connection.prepareStatement(createCustomSequenceSql).executeUpdate();
    }

    private void createUsersTable() throws SQLException {
        String createTableSql =
                """
                        CREATE TABLE IF NOT EXISTS users
                        (
                            id           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                            name         VARCHAR,
                            creationTime TIMESTAMP DEFAULT now(),
                            phone_number VARCHAR
                        );
                        """;

        String sequence =
                """
                        CREATE SEQUENCE IF NOT EXISTS "users_id_seq"
                        START WITH 1 INCREMENT BY 1
                        """;
        connection.prepareStatement(createTableSql).executeUpdate();
        connection.prepareStatement(sequence).executeUpdate();
    }

    private void createPersonTable() throws SQLException {
        String createTableQuery =
                """
                        CREATE TABLE IF NOT EXISTS person
                        (
                            id       BIGINT ,
                            first_name VARCHAR(255),
                            last_name  VARCHAR(255)
                        );
                         """;

        PreparedStatement statement = connection.prepareStatement(createTableQuery);
        statement.executeUpdate();
    }

    private void createParentTable() throws SQLException {
        String createTableQuery =
                """
                        CREATE TABLE IF NOT EXISTS parent 
                        (
                            id   BIGINT PRIMARY KEY,
                            name VARCHAR(255)
                        );
                         """;

        PreparedStatement statement = connection.prepareStatement(createTableQuery);
        statement.executeUpdate();
    }

    private void createChildTable() throws SQLException {
        String createTableQuery =
                """
                        CREATE TABLE IF NOT EXISTS child 
                        (
                            id        BIGINT PRIMARY KEY ,
                            name      VARCHAR(255),
                            parent_id INT REFERENCES parent(id)
                        );
                         """;

        PreparedStatement statement = connection.prepareStatement(createTableQuery);
        statement.executeUpdate();
    }

    private void dropTables() {
        List<String> queries = List.of("DROP TABLE person", "DROP TABLE users", "DROP TABLE customers",
                "DROP TABLE employees", "DROP SEQUENCE \"custom_seq\"", "DROP SEQUENCE \"customers_seq\"", "DROP TABLE child", "DROP TABLE parent");
        queries.forEach(query -> {
            try {
                connection.prepareStatement(query).executeUpdate();
            } catch (SQLException e) {
                throw new BibernateException(e);
            }
        });

    }
}
