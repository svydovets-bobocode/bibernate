package com.bobocode.svydovets.bibernate.validation;

import com.bobocode.svydovets.bibernate.state.EntityState;

/** Entity state validator. Validates state transition from one state to another. */
public interface EntityStateValidator {

    /**
     * Validates if it allowed to change an entity from one state to another state.
     *
     * <p>Example: An Entity in DETACHED state. We can use this method to validate if we can change
     * this state for example to MANAGED. If this is not allowed then the exception will be thrown. In
     * current example validate(EntityState.DETACHED, EntityState.MANAGED); will not throw an
     * exception because this is valid state transition.
     *
     * <p>This class uses {@link
     * com.bobocode.svydovets.bibernate.validation.state.EntityStateTransition} Entity state
     * transition class determines all entity state transitions.
     *
     * @param fromState the current state to change from.
     * @param toState state to an entity to be change to.
     */
    void validate(EntityState fromState, EntityState toState);
}
