package com.bobocode.svydovets.bibernate.locking.optimistic;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.testdata.entity.validation.version.Product;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OptimisticLockServiceTest {
    private static final int SNAPSHOT_VERSION_VALUE = 1;
    private final OptimisticLockService underTest = new OptimisticLockService();

    @Test
    @DisplayName("Value for field annotated with @Version should be taken from snapshot")
    void setVersionValueFromSnapshot() {
        Product product = new Product(1L, 5, 2);
        EntityKey<Product> entityKey = EntityKey.of(Product.class, product.getId());
        Map<String, Object> fieldsMap =
                Map.of("id", 1L, "quantity", 5, "version", SNAPSHOT_VERSION_VALUE);
        Map<EntityKey<?>, Map<String, Object>> snapshot = Map.of(entityKey, fieldsMap);

        underTest.syncVersionValueWithSnapshotIfNeeds(product, entityKey, snapshot);

        Assertions.assertThat(product.getVersion()).isEqualTo(SNAPSHOT_VERSION_VALUE);
    }
}
