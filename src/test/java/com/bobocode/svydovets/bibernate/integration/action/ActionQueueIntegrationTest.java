package com.bobocode.svydovets.bibernate.integration.action;

import static com.bobocode.svydovets.bibernate.integration.action.InsertActionIntegrationTest.DEFAULT_ID;
import static com.bobocode.svydovets.bibernate.integration.action.InsertActionIntegrationTest.DEFAULT_USER_ENTITY_KEY;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.DEFAULT_ENTITY_KEY;
import static org.junit.jupiter.api.Assertions.*;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.action.*;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory;
import com.bobocode.svydovets.bibernate.testdata.factory.TestUserFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ActionQueueIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testActionQueueInsertExecution() throws SQLException {
        Person person = TestPersonFactory.newDefaultPerson();
        User user = TestUserFactory.newDefaultValidUser();
        user.setId(DEFAULT_ID);

        ActionQueue actionQueue = new ActionQueue();

        actionQueue.addAction(
                EntityKey.valueOf(person), new InsertAction<>(dataSource.getConnection(), person));
        actionQueue.addAction(
                EntityKey.valueOf(user), new InsertAction<>(dataSource.getConnection(), user));

        actionQueue.executeAllWithOrder();

        Person savedPerson = searchService.findOne(DEFAULT_ENTITY_KEY);
        User savedUser = searchService.findOne(DEFAULT_USER_ENTITY_KEY);

        assertNotNull(savedPerson);
        assertNotNull(savedUser);
    }

    @Test
    void testActionQueueExecutionOrder() {
        List<ActionType> expectedOrder =
                List.of(ActionType.INSERT, ActionType.UPDATE, ActionType.REMOVE);

        Person person = TestPersonFactory.newDefaultPerson();

        TestAction insertAction = new TestAction(ActionType.INSERT);
        TestAction updateAction = new TestAction(ActionType.UPDATE);
        TestAction removeAction = new TestAction(ActionType.REMOVE);

        ActionQueue actionQueue = new ActionQueue();
        actionQueue.addAction(EntityKey.valueOf(person), removeAction);
        actionQueue.addAction(EntityKey.valueOf(person), updateAction);
        actionQueue.addAction(EntityKey.valueOf(person), insertAction);

        actionQueue.executeAllWithOrder();

        assertEquals(expectedOrder, insertAction.getExecutionOrder());
    }

    static class TestAction implements Action {
        private final ActionType actionType;
        private static final List<ActionType> executionOrder = new ArrayList<>();

        TestAction(ActionType actionType) {
            this.actionType = actionType;
        }

        @Override
        public void execute() {
            executionOrder.add(actionType);
        }

        @Override
        public ActionType getActionType() {
            return actionType;
        }

        List<ActionType> getExecutionOrder() {
            return executionOrder;
        }
    }
}
