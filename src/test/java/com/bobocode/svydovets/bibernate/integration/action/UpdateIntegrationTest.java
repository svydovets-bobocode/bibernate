package com.bobocode.svydovets.bibernate.integration.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bobocode.svydovets.bibernate.AbstractIntegrationTest;
import com.bobocode.svydovets.bibernate.action.UpdateAction;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.testdata.entity.validation.version.Product;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testUpdateWithVersion() throws SQLException {
        connection.prepareStatement("INSERT INTO product VALUES (1, 5, 1)").execute();

        Product product = new Product(1L, 100, 1);
        var updateAction = new UpdateAction(connection, product);

        updateAction.execute();

        Product productInDb = searchService.findOne(new EntityKey<>(Product.class, 1L));
        assertThat(productInDb.getQuantity()).isEqualTo(100);
        assertThat(productInDb.getVersion()).isEqualTo(2);
    }

    @Test
    void testUpdateWithOutdatedVersion() throws SQLException {
        connection.prepareStatement("INSERT INTO product VALUES (1, 5, 2)").execute();

        Product product = new Product(1L, 100, 1);
        var updateAction = new UpdateAction(connection, product);

        assertThatThrownBy(() -> updateAction.execute())
                .isInstanceOf(BibernateException.class)
                .hasMessage("Unable to update entity: Product with id: 1");
    }
}
