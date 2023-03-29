package com.bobocode.svydovets.bibernate.state;

import static com.bobocode.svydovets.bibernate.state.EntityState.TRANSIENT;

import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EntityStateServiceImplTest {

    EntityStateService entityStateService = new EntityStateServiceImpl();

    @Test
    void test() {
        Person person = new Person(2L, "reallyFirstName", "justLastName");
        Assertions.assertEquals(TRANSIENT, entityStateService.getEntityState(person));
    }
}
