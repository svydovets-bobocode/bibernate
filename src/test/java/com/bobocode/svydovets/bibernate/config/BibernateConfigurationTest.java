package com.bobocode.svydovets.bibernate.config;

import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.getValidPostgresProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.session.SessionFactory;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.getValidPostgresProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BibernateConfigurationTest {

    private DataSource dataSource;
    private SqlQueryBuilder sqlQueryBuilder;

    @BeforeEach
    public void setUp() {
        ConfigurationSource source =
                new PropertyFileConfiguration("test_svydovets_bibernate_h2.properties");
        dataSource = new HikariConnectionPool().getDataSource(source);
        sqlQueryBuilder = new SqlQueryBuilder();
    }

    @Test
    public void testSessionConnection() throws SQLException {
        createTestTableAndInsertData();
        BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
        config.configure();
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();

        var testRecord = session.find(User.class, 1);
        assertNotNull(testRecord);
        assertEquals(1, testRecord.getId());
        dropTestTable();
    }

    @Test
        public void testConfigureDefaultPropertyFile () {
            BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
            config.configure();
            SessionFactory sessionFactory = config.buildSessionFactory();
            assertEquals(dataSource, getFieldValue(sessionFactory, "dataSource", DataSource.class));
            assertEquals(
                    sqlQueryBuilder, getFieldValue(sessionFactory, "sqlQueryBuilder", SqlQueryBuilder.class));
        }

        @Test
        public void testConfigureWithPropertyFileConfiguration () {
            ConfigurationSource source =
                    new PropertyFileConfiguration("test_svydovets_bibernate_h2.properties");
            BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
            config.configure(source);
            SessionFactory sessionFactory = config.buildSessionFactory();
            assertEquals(dataSource, getFieldValue(sessionFactory, "dataSource", DataSource.class));
            assertEquals(
                    sqlQueryBuilder, getFieldValue(sessionFactory, "sqlQueryBuilder", SqlQueryBuilder.class));
        }

        @Test
        public void testConfigureWithHashMapConfiguration () {

            ConfigurationSource source = new JavaConfiguration(getValidPostgresProperties());
            BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
            config.configure(source);
            SessionFactory sessionFactory = config.buildSessionFactory();
            assertEquals(dataSource, getFieldValue(sessionFactory, "dataSource", DataSource.class));
            assertEquals(
                    sqlQueryBuilder, getFieldValue(sessionFactory, "sqlQueryBuilder", SqlQueryBuilder.class));
        }

        @Test
        public void testBuildSessionFactoryWithoutConfigure () {
            BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
            assertThrows(IllegalStateException.class, config::buildSessionFactory);
        }

        private static <T > T getFieldValue(Object instance, String fieldName, Class < T > fieldType) {
            try {
                Field field = instance.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return fieldType.cast(field.get(instance));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to get field value using reflection.", e);
            }
        }

        // Todo: move it to abstract method if needed

    private void createTestTableAndInsertData () throws SQLException {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {

                String createTableSql =
                        """
                                CREATE TABLE users
                                (
                                    id           INT PRIMARY KEY,
                                    name         VARCHAR,
                                    creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                                    phone_number VARCHAR
                                )
                                """;
                String insertDataSql = "INSERT INTO users (id, name) VALUES (1, 'Test')";

                statement.execute(createTableSql);
                statement.execute(insertDataSql);
            }
        }
    private void dropTestTable() {
        String dropTableUsers = "DROP TABLE users";
        try (var statement =
                     dataSource.getConnection().prepareStatement(dropTableUsers)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new BibernateException("Unable to drop table");
        }
    }
}
