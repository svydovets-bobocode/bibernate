package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import com.bobocode.svydovets.bibernate.config.PropertyFileConfiguration;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.constant.ErrorMessage;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import java.sql.Connection;
import java.util.HashMap;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

@Tag("unit")
public class SessionTest {

    private SelectAction selectAction;
    private Session session;

    @BeforeEach
    void setUp() {
        ConfigurationSource source =
                new PropertyFileConfiguration("test_svydovets_bibernate_h2.properties");
        DataSource dataSource = new HikariConnectionPool().getDataSource(source);
        selectAction = mock(SelectAction.class);
        Connection connection = mock(Connection.class);
        session = new SessionImpl(selectAction, connection);
    }

    @Test
    @DisplayName("Find Loads Entity from Db by Id")
    void findLoadsEntityFromDbById() {
        // given
        var person = newDefaultPerson();
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenReturn(person);
        // when
        var foundPerson = session.find(Person.class, DEFAULT_ID);
        // then
        Assertions.assertEquals(person, foundPerson);
    }

    @Test
    @DisplayName("Find Returns Entity from Cache when it is loaded")
    void findReturnsEntityFromCacheWhenItIsLoaded() {
        // given
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenAnswer(in -> newDefaultPerson());
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
        when(selectAction.execute(DEFAULT_ENTITY_KEY))
                .thenAnswer(invocationOnMock -> newDefaultPerson());
        // when
        session.find(Person.class, DEFAULT_ID);
        session.find(Person.class, DEFAULT_ID);
        // then
        verify(selectAction, atMostOnce()).execute(DEFAULT_ENTITY_KEY);
    }

    @Test
    @DisplayName("Session methods requires opened session")
    void shouldFailIfSessionIsClosed() {
        // given
        var person = newDefaultPerson();
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenReturn(person);
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
        assertThatThrownBy(() -> session.findAll(Person.class, new HashMap<>()))
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
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenReturn(managedPerson);

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
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenReturn(managedPerson);
        session.find(Person.class, 123L); // Put entity into cache

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
        Person transientPerson = new Person();
        transientPerson.setFirstName("John");
        transientPerson.setLastName("Doe");

        session = Mockito.spy(session);
        when(session.save(transientPerson)).thenReturn(newDefaultPerson());

        // when
        Person managedPerson = session.merge(transientPerson);

        // then
        assertThat(managedPerson).isNotNull();
        assertThat(managedPerson.getId()).isNotNull();
        assertThat(managedPerson.getFirstName()).isEqualTo(transientPerson.getFirstName());
        assertThat(managedPerson.getLastName()).isEqualTo(transientPerson.getLastName());
    }

    @AfterEach
    void tearDown() {}
}
