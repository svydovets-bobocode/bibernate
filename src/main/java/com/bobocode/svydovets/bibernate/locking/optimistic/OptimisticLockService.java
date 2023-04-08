package com.bobocode.svydovets.bibernate.locking.optimistic;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.annotation.Version;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class OptimisticLockService {
    /**
     * In case when provided entity has a field annotated with {@link
     * com.bobocode.svydovets.bibernate.annotation.Version} annotation updates the value of this field
     * by the value from snapshot.
     *
     * @param entity the entity to update @Value annotated field
     * @param entityKey object that represents a unique entity {@link EntityKey}
     * @param entitiesSnapshotMap map that represents a unique entity to its field values in database
     */
    public <T> void syncVersionValueWithSnapshotIfNeeds(
            T entity,
            EntityKey<?> entityKey,
            Map<EntityKey<?>, Map<String, Object>> entitiesSnapshotMap) {
        Optional<Field> versionField = EntityUtils.findVersionField(entityKey.type());
        versionField.ifPresent(
                field -> {
                    Map<String, Object> fieldsMap = entitiesSnapshotMap.get(entityKey);
                    Object resolvedVersionField =
                            fieldsMap == null
                                    ? Version.INITIAL_VERSION_FIELD_VALUE
                                    : fieldsMap.get(field.getName());
                    EntityUtils.setValueToField(entity, field, resolvedVersionField);
                });
    }
}
