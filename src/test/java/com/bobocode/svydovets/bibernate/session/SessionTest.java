package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.bobocode.svydovets.bibernate.testdata.factory.PersonFactory.DEFAULT_ENTITY_KEY;
import static com.bobocode.svydovets.bibernate.testdata.factory.PersonFactory.newDefaultPerson;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class SessionTest {

    private SelectAction selectAction;

    private Session session;

    @BeforeEach
    void setUp() {
        selectAction = mock(SelectAction.class);
        session = new SessionImpl(selectAction);
    }

    @Test
    @DisplayName("Find Loads Entity from Db by Id")
    void findLoadsEntityFromDbById() {
        //given
        var person = newDefaultPerson();
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenReturn(person);
        //when
        var foundPerson = session.find(Person.class, 123L);
        //then
        Assertions.assertEquals(person, foundPerson);
    }

    @Test
    @DisplayName("Find Returns Entity from Cache when it is loaded")
    void findReturnsEntityFromCacheWhenItIsLoaded() {
        //given
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenAnswer(in -> newDefaultPerson());
        //when
        var person1 = session.find(Person.class, 123L);
        var person2 = session.find(Person.class, 123L);
        //then
        Assertions.assertSame(person1, person2);
    }

    @Test
    @DisplayName("Find does not call Db When Entity is already loaded")
    void findDoesNotCallDbWhenEntityIsAlreadyLoaded() {
        //given
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenAnswer(invocationOnMock -> newDefaultPerson());
        //when
        session.find(Person.class, 123L);
        session.find(Person.class, 123L);
        //then
        verify(selectAction, atMostOnce()).execute(DEFAULT_ENTITY_KEY);
    }

    @AfterEach
    void tearDown() {
    }
}