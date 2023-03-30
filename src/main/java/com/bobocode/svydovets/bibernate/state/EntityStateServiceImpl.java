package com.bobocode.svydovets.bibernate.state;

import static java.util.Objects.nonNull;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.validation.EntityStateValidator;
import com.bobocode.svydovets.bibernate.validation.state.EntityStateValidatorImpl;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityStateServiceImpl implements EntityStateService {
    Map<EntityKey<?>, EntityState> entityStateMap = new ConcurrentHashMap<>();
    EntityStateValidator entityStateValidator;

    public EntityStateServiceImpl() {
        this.entityStateValidator = new EntityStateValidatorImpl();
    }

    @Override
    public EntityState getEntityState(EntityKey<?> entityKey) {
        EntityState entityState = entityStateMap.get(entityKey);
        if (nonNull(entityState)) {
            return entityState;
        }
        return EntityState.TRANSIENT;
    }

    @Override
    public EntityState getEntityState(Object entity) {
        return getEntityState(EntityKey.valueOf(entity));
    }

    @Override
    public void setEntityState(EntityKey<?> entityKey, EntityState toState) {
        EntityState existEntityState = entityStateMap.get(entityKey);
        entityStateValidator.validate(existEntityState, toState);
        entityStateMap.put(entityKey, toState);
    }

    @Override
    public void setEntityState(Object entity, EntityState toState) {
        validate(entity, toState);
        EntityKey<?> entityKey = EntityKey.valueOf(entity);
        if (nonNull(entityKey.id())) {
            setEntityState(entityKey, toState);
        }
    }

    @Override
    public void validate(EntityKey<?> entityKey, EntityState toState) {
        EntityState existEntityState = getEntityState(entityKey);
        entityStateValidator.validate(existEntityState, toState);
    }

    @Override
    public void validate(Object entity, EntityState toState) {
        EntityKey<?> entityKey = EntityKey.valueOf(entity);
        validate(entityKey, toState);
    }

    @Override
    public void clearState() {
        entityStateMap.clear();
    }
}
