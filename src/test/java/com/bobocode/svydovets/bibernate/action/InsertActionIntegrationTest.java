package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.testdata.factory.TestUserFactory;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class InsertActionIntegrationTest extends AbstractIntegrationTest {

    @Test
    // todo: remove it
    @Disabled
    void testInsertSuccessful() throws SQLException {
        var user = TestUserFactory.newDefaultUser();
        Action insertAction = new InsertAction<>(dataSource.getConnection(), user);
        insertAction.execute();

        Assertions.assertEquals(2, user.getId());
    }
}
