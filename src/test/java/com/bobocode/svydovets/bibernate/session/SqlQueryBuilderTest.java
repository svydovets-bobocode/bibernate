package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import org.assertj.core.api.Assertions;
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
        Assertions.assertThat(selectByIdQuery).isEqualTo("SELECT * FROM users WHERE id = ?;");
    }

    @Test
    @DisplayName("Create Select by id query when table name is Explicitly Specified")
    void createSelectByIdQueryWhenTableNameIsExplicitlySpecified() {
        // given
        // when
        var selectByIdQuery = sqlBuilder.createSelectByIdQuery(Person.class);
        // then
        Assertions.assertThat(selectByIdQuery).isEqualTo("SELECT * FROM person WHERE id = ?;");
    }
}
