package com.bobocode.svydovets.bibernate.validation.state;

import static java.lang.String.format;
import static java.util.Objects.isNull;

import com.bobocode.svydovets.bibernate.exception.EntityStateValidationException;
import com.bobocode.svydovets.bibernate.state.EntityState;
import com.bobocode.svydovets.bibernate.validation.EntityStateValidator;

public class EntityStateValidatorImpl implements EntityStateValidator {

    @Override
    public void validate(EntityState fromState, EntityState toState) {
        if (isNonAvailableTransition(fromState, toState)) {
            throw new EntityStateValidationException(
                    format("Can't change entity state from %s to %s", fromState.name(), toState.name()));
            //      throw new EntityStateValidationException(format("%s entity passed to persist:
            // package.MyEntity", fromState.name()));
        }
    }

    private static boolean isNonAvailableTransition(EntityState fromState, EntityState toState) {
        return isNull(fromState) || isNull(toState) || !isTransitionStateAvailable(fromState, toState);
    }

    private static boolean isTransitionStateAvailable(EntityState fromState, EntityState toState) {
        return EntityStateTransition.getAvailableEntityTransitionStates(fromState).contains(toState);
    }
}
