package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.testdata.factory.PersonFactory.DEFAULT_ENTITY_KEY;
import static com.bobocode.svydovets.bibernate.testdata.factory.PersonFactory.newDefaultPerson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import com.bobocode.svydovets.bibernate.config.PropertyFileConfiguration;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.util.Constants;
import java.util.HashMap;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
public class SessionTest {

    private SelectAction selectAction;

    private Session session;

    @BeforeEach
    void setUp() {
        ConfigurationSource source =
                new PropertyFileConfiguration("test_svydovets_bibernate.properties");
        DataSource dataSource = new HikariConnectionPool().getDataSource(source);
        selectAction = mock(SelectAction.class);
        session = new SessionImpl(dataSource, selectAction);
    }

    @Test
    @DisplayName("Find Loads Entity from Db by Id")
    void findLoadsEntityFromDbById() {
        // given
        var person = newDefaultPerson();
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenReturn(person);
        // when
        var foundPerson = session.find(Person.class, 123L);
        // then
        Assertions.assertEquals(person, foundPerson);
    }

    @Test
    @DisplayName("Find Returns Entity from Cache when it is loaded")
    void findReturnsEntityFromCacheWhenItIsLoaded() {
        // given
        when(selectAction.execute(DEFAULT_ENTITY_KEY)).thenAnswer(in -> newDefaultPerson());
        // when
        var person1 = session.find(Person.class, 123L);
        var person2 = session.find(Person.class, 123L);
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
        session.find(Person.class, 123L);
        session.find(Person.class, 123L);
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
        assertThatThrownBy(() -> session.find(Person.class, 123L))
                .isInstanceOf(BibernateException.class)
                .hasMessage(Constants.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.save(new Person()))
                .isInstanceOf(BibernateException.class)
                .hasMessage(Constants.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.delete(123L))
                .isInstanceOf(BibernateException.class)
                .hasMessage(Constants.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.findAll(Person.class))
                .isInstanceOf(BibernateException.class)
                .hasMessage(Constants.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.findAll(Person.class, new HashMap<>()))
                .isInstanceOf(BibernateException.class)
                .hasMessage(Constants.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.merge(Person.class))
                .isInstanceOf(BibernateException.class)
                .hasMessage(Constants.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.detach(Person.class))
                .isInstanceOf(BibernateException.class)
                .hasMessage(Constants.SESSION_IS_CLOSED);
        assertThatThrownBy(() -> session.flush())
                .isInstanceOf(BibernateException.class)
                .hasMessage(Constants.SESSION_IS_CLOSED);
    }

    @AfterEach
    void tearDown() {}
}
