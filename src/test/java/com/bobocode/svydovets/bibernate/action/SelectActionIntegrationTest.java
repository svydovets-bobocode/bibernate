package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SelectActionIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testLoad() {
        SelectAction dao = new SelectAction(dataSource, new SqlQueryBuilder());
        EntityKey<Person> key = new EntityKey<>(Person.class, 1L);
        Person entity = dao.execute(key);
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
    }
}

