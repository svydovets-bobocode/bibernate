package com.bobocode.svydovets.bibernate.session;

import static org.assertj.core.api.Assertions.*;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class SqlQueryBuilderTest {
    private final SqlQueryBuilder sqlBuilder = new SqlQueryBuilder();

    @Test
    @DisplayName("Create Select by Id Query")
    void createSelectByIdQuery() {
        // given
        // when
        var selectByIdQuery = sqlBuilder.createSelectByIdQuery(User.class);
        // then
        assertThat(selectByIdQuery).isEqualTo("SELECT * FROM users WHERE id = ?;");
    }

    @Test
    @DisplayName("Create Select by id query when table name is Explicitly Specified")
    void createSelectByIdQueryWhenTableNameIsExplicitlySpecified() {
        // given
        // when
        var selectByIdQuery = sqlBuilder.createSelectByIdQuery(Person.class);
        // then
        assertThat(selectByIdQuery).isEqualTo("SELECT * FROM person WHERE id = ?;");
    }

    @Test
    @DisplayName("Create Select All Query")
    void createSelectAllQuery() {
        // given
        // when
        var selectAllQuery = sqlBuilder.createSelectAllQuery(User.class);
        // then
        assertThat(selectAllQuery).isEqualTo("SELECT * FROM users;");
    }

    @Test
    @DisplayName("Create Select All query when table name is Explicitly Specified")
    void createSelectAllQueryWhenTableNameIsExplicitlySpecified() {
        // given
        // when
        var selectByAllQuery = sqlBuilder.createSelectAllQuery(Person.class);
        // then
        assertThat(selectByAllQuery).isEqualTo("SELECT * FROM person;");
    }
}
