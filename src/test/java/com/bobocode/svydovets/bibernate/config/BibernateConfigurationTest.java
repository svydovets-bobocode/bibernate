package com.bobocode.svydovets.bibernate.config;

import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.getValidH2Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.session.SessionFactory;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class BibernateConfigurationTest extends AbstractIntegrationTest {

    @Test
    public void testSessionConnection() throws SQLException {
        BibernateConfiguration config = new BibernateConfiguration();
        config.configure(new PropertyFileConfiguration("test_svydovets_bibernate_h2.properties"));
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();

        var testRecord = session.find(User.class, 1);
        assertNotNull(testRecord);
        assertEquals(1, testRecord.getId());
        session.close();
    }

    @Test
    public void testConfigureDefaultPropertyFile() {
        BibernateConfiguration config = new BibernateConfiguration();
        config.configure();
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        assertTrue(session.isOpen());
        session.close();
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
        session.close();
    }

    @Test
    public void testConfigureWithHashMapConfiguration() {

        ConfigurationSource source = new JavaConfiguration(getValidH2Properties());
        BibernateConfiguration config = new BibernateConfiguration();
        config.configure(source);
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        assertTrue(session.isOpen());
        session.close();
    }

    @Test
    public void testBuildSessionFactoryWithoutConfigure() {
        BibernateConfiguration config = new BibernateConfiguration();
        assertThrows(IllegalStateException.class, config::buildSessionFactory);
    }
}
