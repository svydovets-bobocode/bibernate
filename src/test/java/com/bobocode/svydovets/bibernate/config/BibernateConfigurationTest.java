package com.bobocode.svydovets.bibernate.config;

import static com.bobocode.svydovets.bibernate.config.MapHelper.properties;
import static org.junit.jupiter.api.Assertions.*;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.session.SessionFactoryImpl;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BibernateConfigurationTest {

    private DataSource dataSource;
    private SqlQueryBuilder sqlQueryBuilder;

    @BeforeEach
    public void setUp() {
        ConfigurationSource source =
                new PropertyFileConfiguration("test_svydovets_bibernate.properties");
        dataSource = new HikariConnectionPool().getDataSource(source);
        sqlQueryBuilder = new SqlQueryBuilder();
    }

    @Test
    public void testSessionConnection() throws SQLException {
        createTestTableAndInsertData();
        BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
        config.configure();
        SessionFactoryImpl sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();

        var testRecord = session.find(User.class, 1);
        assertNotNull(testRecord);
        assertEquals(1, testRecord.getId());
    }

    @Test
    public void testConfigureDefaultPropertyFile() {
        BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
        config.configure();
        SessionFactoryImpl sessionFactory = config.buildSessionFactory();
        assertEquals(dataSource, getFieldValue(sessionFactory, "dataSource", DataSource.class));
        assertEquals(
                sqlQueryBuilder, getFieldValue(sessionFactory, "sqlQueryBuilder", SqlQueryBuilder.class));
    }

    @Test
    public void testConfigureWithPropertyFileConfiguration() {
        ConfigurationSource source =
                new PropertyFileConfiguration("test_svydovets_bibernate.properties");
        BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
        config.configure(source);
        SessionFactoryImpl sessionFactory = config.buildSessionFactory();
        assertEquals(dataSource, getFieldValue(sessionFactory, "dataSource", DataSource.class));
        assertEquals(
                sqlQueryBuilder, getFieldValue(sessionFactory, "sqlQueryBuilder", SqlQueryBuilder.class));
    }

    @Test
    public void testConfigureWithHashMapConfiguration() {

        ConfigurationSource source = new JavaConfiguration(properties);
        BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
        config.configure(source);
        SessionFactoryImpl sessionFactory = config.buildSessionFactory();
        assertEquals(dataSource, getFieldValue(sessionFactory, "dataSource", DataSource.class));
        assertEquals(
                sqlQueryBuilder, getFieldValue(sessionFactory, "sqlQueryBuilder", SqlQueryBuilder.class));
    }

    @Test
    public void testBuildSessionFactoryWithoutConfigure() {
        BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
        assertThrows(IllegalStateException.class, config::buildSessionFactory);
    }

    private static <T> T getFieldValue(Object instance, String fieldName, Class<T> fieldType) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(instance));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get field value using reflection.", e);
        }
    }

    private void createTestTableAndInsertData() throws SQLException {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {

            String createTableSql = "CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR)";
            String insertDataSql = "INSERT INTO users (id, name) VALUES (1, 'Mykhailo')";

            statement.execute(createTableSql);
            statement.execute(insertDataSql);
        }
    }
}
