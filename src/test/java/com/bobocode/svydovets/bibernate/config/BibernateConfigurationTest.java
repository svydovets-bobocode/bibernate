package com.bobocode.svydovets.bibernate.config;

import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.action.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.session.SessionFactory;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.getValidH2Properties;
import static org.junit.jupiter.api.Assertions.*;
import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.getValidH2Properties;
import static org.junit.jupiter.api.Assertions.*;

public class BibernateConfigurationTest {

public class BibernateConfigurationTest extends AbstractIntegrationTest {

    @Test
    public void testSessionConnection() throws SQLException {
        createTestTableAndInsertData();
        BibernateConfiguration config = new BibernateConfiguration();
        config.configure(new PropertyFileConfiguration("test_svydovets_bibernate_h2.properties"));
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();

        var testRecord = session.find(User.class, 1);
        assertNotNull(testRecord);
        assertEquals(1, testRecord.getId());
    }

    @Test
    public void testConfigureDefaultPropertyFile() {
        BibernateConfiguration config = new BibernateConfiguration();
        config.configure();
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        assertTrue(session.isOpen());
    }

    @Test
    public void testConfigureWithPropertyFileConfiguration() {
        ConfigurationSource source =
                new PropertyFileConfiguration("test_svydovets_bibernate_h2.properties");
        BibernateConfiguration config = new BibernateConfiguration();
        config.configure(source);
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        assertTrue(session.isOpen());
    }

    @Test
    public void testConfigureWithHashMapConfiguration() {

        ConfigurationSource source = new JavaConfiguration(getValidH2Properties());
        BibernateConfiguration config = new BibernateConfiguration();
        config.configure(source);
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        assertTrue(session.isOpen());
    }

    @Test
    public void testBuildSessionFactoryWithoutConfigure() {
        BibernateConfiguration config = new BibernateConfiguration();
        assertThrows(IllegalStateException.class, config::buildSessionFactory);
    }

    // Todo: move it to abstract method if needed

    private void createTestTableAndInsertData() throws SQLException {
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
            String insertDataSql = "INSERT INTO users (id, name, phone_number) VALUES (1, 'Test', '123-123-123')";

            statement.execute(createTableSql);
            statement.execute(insertDataSql);
        }
    }
}
