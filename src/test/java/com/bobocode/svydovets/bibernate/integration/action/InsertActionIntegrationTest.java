package com.bobocode.svydovets.bibernate.integration.action;

import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.DEFAULT_ENTITY_KEY;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.DEFAULT_FIRST_NAME;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.DEFAULT_LAST_NAME;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestUserFactory.DEFAULT_NAME;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestUserFactory.DEFAULT_PHONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.action.Action;
import com.bobocode.svydovets.bibernate.action.InsertAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory;
import com.bobocode.svydovets.bibernate.testdata.factory.TestUserFactory;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class InsertActionIntegrationTest extends AbstractIntegrationTest {

    public static final Integer DEFAULT_ID = 100;
    public static final EntityKey<User> DEFAULT_USER_ENTITY_KEY =
            new EntityKey<>(User.class, DEFAULT_ID);

    @Test
    void testInsertSuccessful() throws SQLException {
        var person = TestPersonFactory.newDefaultPerson();
        Action insertAction = new InsertAction<>(dataSource.getConnection(), person);
        insertAction.execute();

        Person savedPerson = searchService.findOne(DEFAULT_ENTITY_KEY);

        assertNotNull(savedPerson);
        assertEquals(1, savedPerson.getId());
        assertEquals(DEFAULT_FIRST_NAME, savedPerson.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, savedPerson.getLastName());
    }

    @Test
    void testInsertIntoAutoIncrementedIdColumnWithSpecifyIdSuccessful() throws SQLException {
        var user = TestUserFactory.newDefaultValidUser();
        user.setId(DEFAULT_ID);
        Action insertAction = new InsertAction<>(dataSource.getConnection(), user);
        insertAction.execute();

        User savedUser = searchService.findOne(DEFAULT_USER_ENTITY_KEY);

        assertNotNull(savedUser);
        assertEquals(DEFAULT_ID, savedUser.getId());
        assertEquals(DEFAULT_NAME, savedUser.getName());
        assertEquals(DEFAULT_PHONE, savedUser.getPhone());
    }
}
