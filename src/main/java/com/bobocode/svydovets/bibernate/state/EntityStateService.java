package com.bobocode.svydovets.bibernate.state;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;

/**
 * Entity state management service.
 * This service allows to get, set and validate entity state.
 */
public interface EntityStateService {

    /**
     * Allows to get current entity state by Entity key object.
     * @param entityKey object represents unique entity {@link EntityKey}
     * @return Entity state object {@link EntityState}
     */
    EntityState getEntityState(EntityKey<?> entityKey);

    /**
     * Allows to get current Entity state by entity object.
     * @param entity object represents an entity
     * @return Entity state object {@link EntityState}
     */
    EntityState getEntityState(Object entity);

    /**
     * Allows to set entity state by entity key object.
     * Before setting an Entity state validates if the entity key current state can be
     * changed to entity state that passed as second argument {@link EntityState}
     * @param entityKey object that represents a unique entity {@link EntityKey}
     * @param toState an Entity state to be changed to.
     */
    void setEntityState(EntityKey<?> entityKey, EntityState toState);

    /**
     * Allows to set Entity state by entity object.
     * Before setting an entity state validates if the entity key current state can be
     * changed to entity state that passed as second argument {@link EntityState}
     * @param entity entity object.
     * @param toState an Entity state to be change to on Entity object.
     */
    void setEntityState(Object entity, EntityState toState);

    /**
     * Validates if an Entity state by entity key can be changed to passed state {@link EntityState}.
     * @param entityKey object that represents a unique entity {@link EntityKey}
     * @param toState an Entity state to be change to on Entity object.
     */
    void validate(EntityKey<?> entityKey, EntityState toState);

    /**
     * Validates if an Entity by Entity Key can be changed to passed state {@link EntityState}.
     * @param entity entity object
     * @param toState an Entity state to be change to on Entity object.
     */
    void validate(Object entity, EntityState toState);

    /**
     * Clears all entities states.
     */
    void clearState();
}
