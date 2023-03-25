package com.bobocode.svydovets.bibernate.action;

import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.DEFAULT_ENTITY_KEY;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.INVALID_ENTITY_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testDeleteById() {
        Person person = selectAction.execute(DEFAULT_ENTITY_KEY);

        deleteAction.execute(DEFAULT_ENTITY_KEY);

        assertNotNull(person);
        assertThrows(
                BibernateException.class,
                () -> selectAction.execute(DEFAULT_ENTITY_KEY),
                "Unable to find entity: Person by id: 123");
    }

    @Test
    void testDeleteByIdShouldThrowNotFoundException() {
        assertThrows(
                BibernateException.class,
                () -> deleteAction.execute(INVALID_ENTITY_KEY),
                "Unable to delete entity: Person by id: 123");
    }
}
