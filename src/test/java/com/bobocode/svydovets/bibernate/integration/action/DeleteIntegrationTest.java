package com.bobocode.svydovets.bibernate.integration.action;

import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.DEFAULT_ENTITY_KEY;
import static com.bobocode.svydovets.bibernate.testdata.factory.TestPersonFactory.DEFAULT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.action.DeleteAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.validation.version.Product;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testDeleteById() {
        Person person = searchService.findOne(DEFAULT_ENTITY_KEY);
        var deleteAction = new DeleteAction(connection, person);

        deleteAction.execute();

        assertThat(person).isNotNull();
        assertThatThrownBy(() -> searchService.findOne(DEFAULT_ENTITY_KEY))
                .isInstanceOf(BibernateException.class)
                .hasMessage("Unable to find entity: Person by id: " + DEFAULT_ID);
    }

    @Test
    void testDeleteByIdWithVersion() throws SQLException {
        connection.prepareStatement("INSERT INTO product VALUES (1, 5, 1)").execute();

        Product product = new Product(1L, 5, 1);
        var deleteAction = new DeleteAction(connection, product);
        assertThat(searchService.findOne(new EntityKey<>(Product.class, 1L))).isNotNull();

        deleteAction.execute();

        assertThat(product).isNotNull();
        assertThatThrownBy(() -> searchService.findOne(new EntityKey<>(Product.class, 1L)))
                .isInstanceOf(BibernateException.class)
                .hasMessage("Unable to find entity: Product by id: 1");
    }

    @Test
    void testDeleteByIdWithOutdatedVersion() throws SQLException {
        connection.prepareStatement("INSERT INTO product VALUES (1, 5, 2)").execute();

        Product product = new Product(1L, 5, 1);
        var deleteAction = new DeleteAction(connection, product);

        assertThat(product).isNotNull();
        assertThatThrownBy(() -> deleteAction.execute())
                .isInstanceOf(BibernateException.class)
                .hasMessage("Unable to delete entity: Product by id: 1");
        assertThat(searchService.findOne(new EntityKey<>(Product.class, 1L))).isNotNull();
    }
}
