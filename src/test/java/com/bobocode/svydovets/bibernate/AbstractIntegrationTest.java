package com.bobocode.svydovets.bibernate;

import com.bobocode.svydovets.bibernate.action.mapper.ResultSetMapper;
import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import com.bobocode.svydovets.bibernate.config.PropertyFileConfiguration;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.session.SearchService;
import com.bobocode.svydovets.bibernate.session.SessionImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import javax.sql.DataSource;
import java.sql.*;

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
        createParentTable();
        createChildTable();
    }

    private void insertIntoTables() throws SQLException {
        String insertPersonsQuery =
                "INSERT INTO person(id, first_name, last_name) VALUES (1, 'John', 'Doe'), (2, 'Martin', 'Fowler')";
        connection.prepareStatement(insertPersonsQuery).execute();

        String insertUsersQuery = "INSERT INTO users (name) VALUES ('Test')";
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

    private void dropTables() throws SQLException {
        String dropTablePerson = "DROP TABLE person";
        String dropTableUsers = "DROP TABLE users";
        String dropTableChild = "DROP TABLE child";
        String dropTableParent = "DROP TABLE parent";
        connection.prepareStatement(dropTablePerson).executeUpdate();
        connection.prepareStatement(dropTableUsers).executeUpdate();
        connection.prepareStatement(dropTableChild).executeUpdate();
        connection.prepareStatement(dropTableParent).executeUpdate();
    }
}
