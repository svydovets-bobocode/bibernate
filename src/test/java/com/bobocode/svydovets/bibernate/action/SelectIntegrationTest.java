package com.bobocode.svydovets.bibernate.action;

import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.session.SessionImpl;
import com.bobocode.svydovets.bibernate.testdata.entity.Child;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

class SelectIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testFindById() {
        Person entity = searchService.findOne(DEFAULT_ENTITY_KEY);
        assertPerson(entity, DEFAULT_ID, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
    }

    @Test
    void testFindByIdShouldThrowNotFoundException() {
        assertThrows(
                BibernateException.class,
                () -> searchService.findOne(INVALID_ENTITY_KEY),
                "Unable to find entity: Person by id: -1");
    }

    @Test
    void testFindByIdWithManyToOneRelation() {
        Child child = searchService.findOne(DEFAULT_CHILD_ENTITY_KEY);
        assertEquals(DEFAULT_CHILD_ID, child.getId());
        assertEquals("Test1", child.getName());
        assertNotNull(child.getParent());
    }

    @Test
    void testFindAll() {
        Session session = new SessionImpl(connection, searchService);

        Collection<Person> personCollection = session.findAll(Person.class);

        List<Person> retrievedPersons = personCollection.stream().toList();

        assertEquals(2, retrievedPersons.size());
        assertPerson(retrievedPersons.get(0), DEFAULT_ID, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        assertPerson(retrievedPersons.get(1), OTHER_ID, OTHER_FIRST_NAME, OTHER_LAST_NAME);
    }

    private static void assertPerson(Person entity, Long id, String firstName, String lastName) {
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getFirstName()).isEqualTo(firstName);
        assertThat(entity.getLastName()).isEqualTo(lastName);
    }
}
