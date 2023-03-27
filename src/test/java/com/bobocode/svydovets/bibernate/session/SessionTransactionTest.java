package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
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
        session = new SessionImpl(connection, searchService);
    }

    @Test
    @DisplayName("Begin and commit")
    void beginAndCommit() throws SQLException {
        List<Person> personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());

        session.begin();

        saveDefaultPersonIntoDb();
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

        saveDefaultPersonIntoDb();
        personsFromDb = getPersonsFromDb();
        assertEquals(3, personsFromDb.size());

        session.rollback();

        personsFromDb = getPersonsFromDb();
        assertEquals(2, personsFromDb.size());
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
            var id = rs.getLong(1);
            var firstName = rs.getString(2);
            var secondName = rs.getString(3);
            persons.add(new Person(id, firstName, secondName));
        }
        return persons;
    }
}
