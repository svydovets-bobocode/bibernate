package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class InsertActionIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testInsertSuccessful() throws SQLException {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        Action insertAction = new InsertAction(dataSource.getConnection(), person);
        insertAction.execute();
    }
}
