package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.sql.SQLException;

import com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InsertActionIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testInsertSuccessful() throws SQLException {
        Person person = TestPersonFactory.newDefaultPerson();
        Action insertAction = new InsertAction(dataSource.getConnection(), person);
        insertAction.execute();

        Assertions.assertEquals(3, person.getId());
    }
}
