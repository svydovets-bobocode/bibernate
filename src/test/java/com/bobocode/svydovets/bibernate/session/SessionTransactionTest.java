package com.bobocode.svydovets.bibernate.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.config.BibernateConfiguration;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTransactionTest extends AbstractIntegrationTest {
    private static SessionFactory sessionFactory;
    private Session session;

    // todo: rework connection in tests after adding find(), save() to the session
    @BeforeAll
    static void initBeforeAll() {
        sessionFactory = getSessionFactory();
    }

    private static SessionFactory getSessionFactory() {
        BibernateConfiguration config = new BibernateConfiguration();
        config.configure();
        return config.buildSessionFactory();
    }

    @BeforeEach
    void beforeEach() {
        session = sessionFactory.openSession();
    }

    @Test
    @DisplayName("Begin and commit")
    void beginAndCommit() throws SQLException {
        List<Person> personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());

        session.begin();

        savePersonIntoDb();
        personsFromDb = getPersonsFromDb();
        assertEquals(3, personsFromDb.size());

        session.commit();

        personsFromDb = getPersonsFromDb();
        assertEquals(3, personsFromDb.size());
    }

    @Test
    @DisplayName("Begin and rollback")
    void beginAndRollback() throws SQLException {
        List<Person> personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());

        session.begin();

        savePersonIntoDb();
        personsFromDb = getPersonsFromDb();
        assertEquals(3, personsFromDb.size());

        session.rollback();

        personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());
    }

    private void savePersonIntoDb() {
        session.save(new Person(3L, "test", "testovich"));
    }

    private List<Person> getPersonsFromDb() {
        return (List<Person>) session.findAll(Person.class);
    }
}
