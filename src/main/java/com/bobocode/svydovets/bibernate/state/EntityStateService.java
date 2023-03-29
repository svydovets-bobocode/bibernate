package com.bobocode.svydovets.bibernate.state;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;

public interface EntityStateService {

    EntityState getEntityState(EntityKey<?> entityKey);

    EntityState getEntityState(Object entity);

    void setEntityState(EntityKey<?> entityKey, EntityState entityState);

    void setEntityState(Object entity, EntityState entityState);

    void validate(EntityState fromState, EntityState toState);

    void validate(EntityKey<?> entityKey, EntityState toState);

    void validate(Object entity, EntityState toState);

    void clearState();
}
