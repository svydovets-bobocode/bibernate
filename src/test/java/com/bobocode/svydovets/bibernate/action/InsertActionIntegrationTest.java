package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InsertActionIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testInsertSuccessful() throws SQLException {
        var user = TestPersonFactory.newDefaultPerson();
        Action insertAction = new InsertAction(dataSource.getConnection(), user);
        insertAction.execute();

        Assertions.assertEquals(3, user.getId());
    }

    @Test
    void testInsertEntityWithProvidedIdShouldThrowException() throws SQLException {
        var user = TestPersonFactory.newDefaultInvalidPerson();
        Action insertAction = new InsertAction(dataSource.getConnection(), user);

        Assertions.assertThrows(BibernateException.class, insertAction::execute);
    }
}
