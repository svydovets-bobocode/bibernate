package com.bobocode.svydovets.bibernate.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.session.Session;
import com.bobocode.svydovets.bibernate.session.SessionImpl;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.util.List;
import org.junit.jupiter.api.Test;

class SelectActionIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testLoad() {
        SelectAction dao = new SelectAction(dataSource, new SqlQueryBuilder());
        EntityKey<Person> key = new EntityKey<>(Person.class, 1L);
        Person entity = dao.execute(key);
        assertPerson(entity, 1L, "John", "Doe");
    }

    @Test
    void testFindAll() {
        SelectAction dao = new SelectAction(dataSource, new SqlQueryBuilder());
        Session session = new SessionImpl(dao, connection);

        List<Person> retrievedPersons = session.findAll(Person.class);

        assertEquals(2, retrievedPersons.size());
        assertPerson(retrievedPersons.get(0), 1L, "John", "Doe");
        assertPerson(retrievedPersons.get(1), 2L, "Martin", "Fowler");
    }

    private static void assertPerson(Person entity, Long id, String firstName, String lastName) {
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getFirstName()).isEqualTo(firstName);
        assertThat(entity.getLastName()).isEqualTo(lastName);
    }
}
