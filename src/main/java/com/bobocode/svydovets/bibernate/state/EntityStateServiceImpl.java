package com.bobocode.svydovets.bibernate.state;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.exception.EntityStateValidationException;
import com.bobocode.svydovets.bibernate.validation.EntityStateTransitionValidator;
import com.bobocode.svydovets.bibernate.validation.state.EntityStateTransitionTransitionValidatorImpl;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityStateServiceImpl implements EntityStateService {
    Map<EntityKey<?>, EntityState> entityStateMap = new ConcurrentHashMap<>();
    EntityStateTransitionValidator entityStateTransitionValidator;

    public EntityStateServiceImpl() {
        this.entityStateTransitionValidator = new EntityStateTransitionTransitionValidatorImpl();
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
        entityStateTransitionValidator.validate(existEntityState, toState);
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
        entityStateTransitionValidator.validate(existEntityState, toState);
    }

    @Override
    public void validate(EntityKey<?> entityKey, EntityState desiredState, EntityState toState) {
        EntityState existEntityState = getEntityState(entityKey);
        if (desiredState.equals(existEntityState)) {
            entityStateTransitionValidator.validate(existEntityState, toState);
        } else {
            throw new EntityStateValidationException(
                    format("Entity state should be in %s, but was in %s", desiredState, existEntityState));
        }
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
