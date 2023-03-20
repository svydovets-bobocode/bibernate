package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.testdata.factory.PersonFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.bobocode.svydovets.bibernate.action.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTransactionTest extends AbstractIntegrationTest {
    private static SessionFactory sessionFactory;
    private Session session;

    // todo: rework connection in tests after adding find(), save() to the session
    //    @BeforeAll
    //    static void initBeforeAll() {
    //        sessionFactory = getSessionFactory();
    //    }
    //
    //    private static SessionFactory getSessionFactory() {
    //        SqlQueryBuilder sqlQueryBuilder = new SqlQueryBuilder();
    //        BibernateConfiguration config = new BibernateConfiguration(dataSource, sqlQueryBuilder);
    //        config.configure();
    //        return config.buildSessionFactory();
    //    }

    //    @BeforeEach
    //    void beforeEach() {
    //        session = sessionFactory.openSession();
    //    }

    @BeforeEach
    void beforeEach() {
        SelectAction selectAction = mock(SelectAction.class);
        session = new SessionImpl(selectAction, connection);
    }

    @Test
    @DisplayName("Begin and commit")
    void beginAndCommit() throws SQLException {
        List<Person> personsFromDb = getPersonsFromDb();
        assertEquals(1, personsFromDb.size());

        session.begin();

        saveDefaultPersonIntoDb();
        personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());

        session.commit();

        personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());
    }

    @Test
    @DisplayName("Begin and rollback")
    void beginAndRollback() throws SQLException {
        List<Person> personsFromDb = getPersonsFromDb();
        assertEquals(1, personsFromDb.size());

        session.begin();

        saveDefaultPersonIntoDb();
        personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());

        session.rollback();

        personsFromDb = getPersonsFromDb();
        assertEquals(1, personsFromDb.size());
    }

    private void saveDefaultPersonIntoDb() throws SQLException {
        String insertQuery = "INSERT INTO person VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insertQuery);
        statement.setLong(1, DEFAULT_ID);
        statement.setString(2, DEFAULT_FIRST_NAME);
        statement.setString(3, DEFAULT_LAST_NAME);
        statement.executeUpdate();
    }

    private List<Person> getPersonsFromDb() throws SQLException {
        ArrayList<Person> persons = new ArrayList<>();
        ResultSet rs = connection.prepareStatement("select * from person").executeQuery();
        while (rs.next()) {
            long id = rs.getLong(1);
            String fN = rs.getString(2);
            String sN = rs.getString(3);
            persons.add(new Person(id, fN, sN));
        }
        return persons;
    }
}
