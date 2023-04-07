package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.*;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.setValueToField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.bobocode.svydovets.bibernate.action.ActionQueue;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.constant.ErrorMessage;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.session.service.SearchService;
import com.bobocode.svydovets.bibernate.state.EntityStateServiceImpl;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

@Tag("unit")
public class SessionTest {

    private SearchService searchService;
    private Session session;
    private EntityStateServiceImpl mockedEntityStateService;
    private ActionQueue actionQueue;

    @BeforeEach
    void setUp() {
        searchService = mock(SearchService.class);
        Connection connection = mock(Connection.class);
        actionQueue = mock(ActionQueue.class);
        session = new SessionImpl(connection, searchService);
    }

    @Test
    @DisplayName("Find Loads Entity from Db by Id")
    void findLoadsEntityFromDbById() {
        // given
        var person = newDefaultPerson();
        when(searchService.findOne(DEFAULT_ENTITY_KEY)).thenReturn(person);
        when(searchService.findOne(eq(DEFAULT_ENTITY_KEY), any())).thenReturn(person);
        // when
        var foundPerson = session.find(Person.class, DEFAULT_ID);
        // then
        Assertions.assertEquals(person, foundPerson);
    }

    @Test
    @DisplayName("Find Returns Entity from Cache when it is loaded")
    void findReturnsEntityFromCacheWhenItIsLoaded() {
        // given
        when(searchService.findOne(DEFAULT_ENTITY_KEY)).thenAnswer(in -> newDefaultPerson());
        when(searchService.findOne(eq(DEFAULT_ENTITY_KEY), any())).thenAnswer(in -> newDefaultPerson());
        // when
        var person1 = session.find(Person.class, DEFAULT_ID);
        var person2 = session.find(Person.class, DEFAULT_ID);
        // then
        Assertions.assertSame(person1, person2);
    }

    @Test
    @DisplayName("Find does not call Db When Entity is already loaded")
    void findDoesNotCallDbWhenEntityIsAlreadyLoaded() {
        // given
        when(searchService.findOne(eq(DEFAULT_ENTITY_KEY), any()))
                .thenAnswer(invocationOnMock -> newDefaultPerson());
        // when
        Person person = session.find(Person.class, DEFAULT_ID);
        Person samePerson = session.find(Person.class, DEFAULT_ID);
        // then
        verify(searchService, atMostOnce()).findOne(DEFAULT_ENTITY_KEY);
        Assertions.assertEquals(MANAGED, session.getEntityState(person));
        Assertions.assertEquals(MANAGED, session.getEntityState(samePerson));
    }

    @Test
    @DisplayName("Session methods requires opened session")
    void shouldFailIfSessionIsClosed() {
        // given
        var person = newDefaultPerson();
        when(searchService.findOne(eq(DEFAULT_ENTITY_KEY), any())).thenReturn(person);
        session.close();
        // when
        // then
        assertThatThrownBy(() -> session.find(Person.class, DEFAULT_ID))
                .isInstanceOf(BibernateException.class)
                .hasMessage(ErrorMessage.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.save(new Person()))
                .isInstanceOf(BibernateException.class)
                .hasMessage(ErrorMessage.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.delete(person))
                .isInstanceOf(BibernateException.class)
                .hasMessage(ErrorMessage.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.findAll(Person.class))
                .isInstanceOf(BibernateException.class)
                .hasMessage(ErrorMessage.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.merge(Person.class))
                .isInstanceOf(BibernateException.class)
                .hasMessage(ErrorMessage.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.detach(Person.class))
                .isInstanceOf(BibernateException.class)
                .hasMessage(ErrorMessage.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.flush())
                .isInstanceOf(BibernateException.class)
                .hasMessage(ErrorMessage.SESSION_IS_CLOSED);
    }

    @Test
    @DisplayName("Merge Detached Entity When Entity Not In Cache")
    void mergeDetachedEntityWhenEntityNotInCache() {
        // given
        Person detachedPerson = newDefaultPerson();
        Person managedPerson = newDefaultPerson();
        when(searchService.findOne(DEFAULT_ENTITY_KEY)).thenReturn(managedPerson);
        when(searchService.findOne(eq(DEFAULT_ENTITY_KEY), any())).thenReturn(managedPerson);

        mockedEntityStateService = Mockito.mock(EntityStateServiceImpl.class);
        setInternalDependency(session, "entityStateService", mockedEntityStateService);

        // when
        Person mergedPerson = session.merge(detachedPerson);

        // then
        assertThat(mergedPerson).isNotSameAs(detachedPerson);
        assertThat(mergedPerson).isEqualTo(detachedPerson);
    }

    @Test
    @DisplayName("Merge Detached Entity When Entity In Cache")
    void mergeDetachedEntityWhenEntityInCache() {
        // given
        Person detachedPerson = newDefaultPerson();
        Person managedPerson = newDefaultPerson();

        when(searchService.findOne(eq(DEFAULT_ENTITY_KEY), any())).thenReturn(managedPerson);
        mockedEntityStateService = Mockito.mock(EntityStateServiceImpl.class);
        setInternalDependency(session, "entityStateService", mockedEntityStateService);

        session.find(Person.class, DEFAULT_ID); // Put entity into cache

        // when
        Person mergedPerson = session.merge(detachedPerson);

        // then
        assertThat(mergedPerson).isNotSameAs(detachedPerson);
        assertThat(mergedPerson).isEqualTo(detachedPerson);
    }

    @Test
    @DisplayName("Merge saves transient entity and returns a managed instance with identifier")
    void mergeSavesTransientEntityAndReturnsManagedInstanceWithId() {
        // given
        Person detachedPerson = new Person();
        detachedPerson.setId(1L);
        detachedPerson.setFirstName("John");
        detachedPerson.setLastName("Doe");

        session = Mockito.spy(session);

        when(searchService.findOne(any())).thenReturn(newDefaultPerson());
        mockedEntityStateService = Mockito.mock(EntityStateServiceImpl.class);
        setInternalDependency(session, "entityStateService", mockedEntityStateService);

        // when
        Person managedPerson = session.merge(detachedPerson);

        // then
        assertThat(managedPerson).isNotNull();
        assertThat(managedPerson.getId()).isNotNull();
        assertThat(managedPerson.getFirstName()).isEqualTo(detachedPerson.getFirstName());
        assertThat(managedPerson.getLastName()).isEqualTo(detachedPerson.getLastName());
    }

    @Test
    @DisplayName(
            "Flush triggers dirty checking, it creates correct update actions and puts it to the ActionQueue")
    void testFlushTriggersDirtyChecking() {
        session = Mockito.spy(session);

        Person firstPerson = newDefaultPerson();
        EntityKey<?> firstEntityKey = EntityKey.of(Person.class, firstPerson.getId());
        doReturn(firstPerson).when(searchService).findOne(eq(firstEntityKey));
        doReturn(firstPerson).when(searchService).findOne(eq(firstEntityKey), any());

        Person secondPerson = newOtherPerson();
        EntityKey<?> secondEntityKey = EntityKey.of(Person.class, secondPerson.getId());
        doReturn(secondPerson).when(searchService).findOne(eq(secondEntityKey));
        doReturn(secondPerson).when(searchService).findOne(eq(secondEntityKey), any());

        mockedEntityStateService = Mockito.mock(EntityStateServiceImpl.class);
        setInternalDependency(session, "entityStateService", mockedEntityStateService);
        setInternalDependency(session, "actionQueue", actionQueue);

        Person firstLoadedPerson = session.find(Person.class, firstPerson.getId());
        firstLoadedPerson.setFirstName(OTHER_FIRST_NAME);

        Person secondLoadedPerson = session.find(Person.class, secondPerson.getId());
        secondLoadedPerson.setFirstName(DEFAULT_FIRST_NAME);

        session.flush();

        verify(actionQueue).addAction(eq(firstEntityKey), any());
        verify(actionQueue).addAction(eq(secondEntityKey), any());
    }

    private void setInternalDependency(Session session, String dependencyName, Object dependency) {
        Field field =
                Arrays.stream(session.getClass().getDeclaredFields())
                        .filter(f -> f.getName().equals(dependencyName))
                        .findAny()
                        .orElseThrow(
                                () -> new IllegalArgumentException("Can't find dependency " + dependencyName));
        setValueToField(session, field, dependency);
    }
}
