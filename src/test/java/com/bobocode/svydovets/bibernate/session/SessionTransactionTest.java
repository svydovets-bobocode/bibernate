package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.state.EntityState.DETACHED;
import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;
import static com.bobocode.svydovets.bibernate.state.EntityState.REMOVED;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.config.BibernateConfiguration;
import com.bobocode.svydovets.bibernate.exception.EntityStateValidationException;
import com.bobocode.svydovets.bibernate.state.EntityState;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.*;

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

    @AfterEach
    void afterEach() {
        session.close();
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

    @Test
    @DisplayName("Check if the selected entity is in the MANAGED state")
    void findAndCheckIfAnEntityIsInManagedState() throws SQLException {
        session.begin();
        Person personsFromDb = session.find(Person.class, 1);
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(MANAGED, actualEntityState);
        session.commit();
    }

    @Test
    @DisplayName("Check if the session detach method set DETACHED state")
    void detachFoundEntityAndCheckIfAnEntityIsInDetachedState() throws SQLException {
        session.begin();
        Person personsFromDb = session.find(Person.class, 1);
        session.detach(personsFromDb);
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(DETACHED, actualEntityState);
        session.commit();
    }

    @Test
    @DisplayName("Check if the entity is in MANAGED state after merge method call")
    void mergeDetachedEntityAndCheckIfAnEntityIsInManagedState() throws SQLException {
        session.begin();
        Person personsFromDb = session.find(Person.class, 1);
        session.detach(personsFromDb);
        session.merge(personsFromDb);
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(MANAGED, actualEntityState);
        session.commit();
    }

    @Test
    @DisplayName("Check if the entity is in REMOVED state after delete method call")
    void removeEntityAndCheckIfAnEntityIsInRemovedState() throws SQLException {
        session.begin();
        Person personsFromDb = session.find(Person.class, 1);
        session.delete(personsFromDb);
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(REMOVED, actualEntityState);
        session.commit();
    }

    @Test
    @DisplayName("Should throw en exception when merge entity in TRANSIENT state")
    void shouldThrowAnExceptionWhenMergeEntityThatIsInTransientState() throws SQLException {
        session.begin();

        Person personsFromDb = new Person(1L, "Transient person", "Last name");

        assertThrows(
                EntityStateValidationException.class,
                () -> session.merge(personsFromDb),
                "Entity state should be in DETACHED, but was in TRANSIENT");
        session.commit();
    }

    @Test
    @DisplayName("Should throw en exception when delete entity in TRANSIENT state")
    void shouldThrowAnExceptionWhenDeleteEntityThatIsInTransientState() throws SQLException {
        session.begin();

        Person personsFromDb = new Person(1L, "Transient person", "Last name");

        assertThrows(
                EntityStateValidationException.class,
                () -> session.delete(personsFromDb),
                "an't change entity state from TRANSIENT to REMOVED");
        session.commit();
    }

    private void savePersonIntoDb() {
        session.save(new Person(333L, "test", "testovich"));
    }

    private List<Person> getPersonsFromDb() {
        return (List<Person>) session.findAll(Person.class);
    }
}
