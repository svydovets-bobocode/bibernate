package com.bobocode.svydovets.bibernate.testdata.factory;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.testdata.entity.Child;
import com.bobocode.svydovets.bibernate.testdata.entity.Parent;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;

public class TestPersonFactory {
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";

    public static final Long OTHER_ID = 2L;
    public static final String OTHER_FIRST_NAME = "Martin";
    public static final String OTHER_LAST_NAME = "Fowler";

    public static final Long INVALID_ID = -1L;

    public static final Long DEFAULT_CHILD_ID = 5L;
    public static final Long SECOND_CHILD_ID = 6L;
    public static final Long DEFAULT_PARENT_ID = 4L;

    public static final EntityKey<Person> DEFAULT_ENTITY_KEY =
            new EntityKey<>(Person.class, DEFAULT_ID);

    public static final EntityKey<Person> INVALID_ENTITY_KEY =
            new EntityKey<>(Person.class, INVALID_ID);

    public static final EntityKey<Child> DEFAULT_CHILD_ENTITY_KEY =
            new EntityKey<>(Child.class, DEFAULT_CHILD_ID);

    public static final EntityKey<Parent> DEFAULT_PARENT_ENTITY_KEY =
            new EntityKey<>(Parent.class, DEFAULT_PARENT_ID);

    public static Person newDefaultPerson() {
        Person person = new Person();
        person.setId(DEFAULT_ID);
        person.setFirstName(DEFAULT_FIRST_NAME);
        person.setLastName(DEFAULT_LAST_NAME);
        return person;
    }

    public static Person newOtherPerson() {
        Person person = new Person();
        person.setId(OTHER_ID);
        person.setFirstName(OTHER_FIRST_NAME);
        person.setLastName(OTHER_LAST_NAME);
        return person;
    }

    public static Person newDefaultInvalidPerson() {
        Person person = new Person();
        person.setFirstName(DEFAULT_FIRST_NAME);
        person.setLastName(DEFAULT_LAST_NAME);
        return person;
    }
}
