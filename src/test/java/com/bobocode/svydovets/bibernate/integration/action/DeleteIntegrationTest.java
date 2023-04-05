package com.bobocode.svydovets.bibernate.integration.action;

import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.DEFAULT_ENTITY_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.action.DeleteAction;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testDeleteById() {
        Person person = searchService.findOne(DEFAULT_ENTITY_KEY);
        var deleteAction = new DeleteAction(connection, person);

        deleteAction.execute();

        assertNotNull(person);
        assertThrows(
                BibernateException.class,
                () -> searchService.findOne(DEFAULT_ENTITY_KEY),
                "Unable to find entity: Person by id: 123");
    }
}
