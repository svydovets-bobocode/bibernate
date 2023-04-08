package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.state.EntityState.DETACHED;
import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;
import static com.bobocode.svydovets.bibernate.state.EntityState.REMOVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.config.BibernateConfiguration;
import com.bobocode.svydovets.bibernate.exception.EntityStateValidationException;
import com.bobocode.svydovets.bibernate.state.EntityState;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTransactionTest extends AbstractIntegrationTest {
    private static SessionFactory sessionFactory;
    private Session session;

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
        sessionFactory = SessionFactoryImpl.getInstance(dataSource);
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

        session.beginTransaction();

        savePersonIntoDb();
        personsFromDb = getPersonsFromDb();
        assertEquals(3, personsFromDb.size());

        session.commitTransaction();

        personsFromDb = getPersonsFromDb();
        assertEquals(3, personsFromDb.size());
    }

    @Test
    @DisplayName("Begin and rollback")
    void beginAndRollback() throws SQLException {
        List<Person> personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());

        session.beginTransaction();

        savePersonIntoDb();
        personsFromDb = getPersonsFromDb();
        assertEquals(3, personsFromDb.size());

        session.rollbackTransaction();

        personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());
    }

    @Test
    @DisplayName(
            "State should be DETACHED after open new session after close when the entity was managed in old session before")
    void stateShouldBeDetachedInNewSessionAfterCloseWhenWasManagedBefore() {
        Session session1 = sessionFactory.openSession();

        Person personsFromDb = session1.find(Person.class, 1L);
        personsFromDb.setFirstName("blablabla");
        EntityState actualEntityState = session1.getEntityState(personsFromDb);
        assertEquals(MANAGED, actualEntityState);
        session1.close();

        EntityState actualEntityState3 = session1.getEntityState(personsFromDb);
        assertEquals(DETACHED, actualEntityState3);

        Session session2 = sessionFactory.openSession();
        Person merged = session2.merge(personsFromDb);
        merged.setFirstName("blablablaMerged");
        EntityState actualEntityState2 = session2.getEntityState(merged);
        assertEquals(MANAGED, actualEntityState2);
        session2.close();
    }

    @Test
    @DisplayName(
            "State should be DETACHED after open new session after close when the entity was managed in old session before")
    void stateShouldBeDetachedInNewSessionAfterCloseWhenWasManagedBeforeTransaction()
            throws SQLException {
        session.beginTransaction();
        Person personsFromDb = session.find(Person.class, 1L);
        personsFromDb.setFirstName("blablabla");
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(MANAGED, actualEntityState);
        session.commitTransaction();

        EntityState actualEntityState3 = session.getEntityState(personsFromDb);
        assertEquals(MANAGED, actualEntityState3);
    }

    @Test
    @DisplayName("Check if the selected entity is in the MANAGED state")
    void findAndCheckIfAnEntityIsInManagedState() throws SQLException {
        session.beginTransaction();
        Person personsFromDb = session.find(Person.class, 1L);
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(MANAGED, actualEntityState);
        session.commitTransaction();
    }

    @Test
    @DisplayName("Check if the session detach method set DETACHED state")
    void detachFoundEntityAndCheckIfAnEntityIsInDetachedState() {
        session.beginTransaction();
        Person personsFromDb = session.find(Person.class, 1L);
        session.detach(personsFromDb);
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(DETACHED, actualEntityState);
        session.commitTransaction();
    }

    @Test
    @DisplayName("Check if the entity is in MANAGED state after merge method call")
    void mergeDetachedEntityAndCheckIfAnEntityIsInManagedState() {
        session.beginTransaction();
        Person personsFromDb = session.find(Person.class, 1L);
        session.detach(personsFromDb);
        session.merge(personsFromDb);
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(MANAGED, actualEntityState);
        session.commitTransaction();
    }

    @Test
    @DisplayName("Check if the entity is in REMOVED state after delete method call")
    void removeEntityAndCheckIfAnEntityIsInRemovedState() {
        session.beginTransaction();
        Person personsFromDb = session.find(Person.class, 1L);
        session.delete(personsFromDb);
        EntityState actualEntityState = session.getEntityState(personsFromDb);
        assertEquals(REMOVED, actualEntityState);
        session.commitTransaction();
    }

    @Test
    @DisplayName("Should throw en exception when merge entity in TRANSIENT state")
    void shouldThrowAnExceptionWhenMergeEntityThatIsInTransientState() {
        session.beginTransaction();

        Person personsFromDb = new Person(2L, "Transient person", "Last name");

        assertThrows(
                EntityStateValidationException.class,
                () -> session.merge(personsFromDb),
                "Entity state should be in DETACHED, but was in TRANSIENT");
        session.commitTransaction();
    }

    @Test
    @DisplayName("Should throw en exception when delete entity in TRANSIENT state")
    void shouldThrowAnExceptionWhenDeleteEntityThatIsInTransientState() {
        session.beginTransaction();

        Person personsFromDb = new Person(1L, "Transient person", "Last name");

        assertThrows(
                EntityStateValidationException.class,
                () -> session.delete(personsFromDb),
                "Can't change entity state from TRANSIENT to REMOVED");
        session.commitTransaction();
    }

    private void savePersonIntoDb() {
        session.save(new Person(333L, "test", "testovich"));
    }

    private List<Person> getPersonsFromDb() {
        return (List<Person>) session.findAll(Person.class);
    }
}
