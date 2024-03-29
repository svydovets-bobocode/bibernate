package com.bobocode.svydovets.bibernate.validation.state;

import static com.bobocode.svydovets.bibernate.state.EntityState.TRANSIENT;
import static com.bobocode.svydovets.bibernate.validation.state.EntityStateTransition.getAvailableEntityTransitionStates;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import com.bobocode.svydovets.bibernate.exception.EntityStateValidationException;
import com.bobocode.svydovets.bibernate.state.EntityState;
import com.bobocode.svydovets.bibernate.validation.EntityStateTransitionValidator;

public class EntityStateTransitionValidatorImpl implements EntityStateTransitionValidator {

    @Override
    public void validate(EntityState fromState, EntityState toState) {
        if (isNonAvailableTransition(fromState, toState)) {
            throw new EntityStateValidationException(getFormattedMessage(fromState, toState));
        }
    }

    private static String getFormattedMessage(EntityState fromState, EntityState toState) {
        if (isNull(fromState)) {
            return format("Can't change entity state from TRANSIENT to %s", toState);
        }
        return format("Can't change entity state from %s to %s", fromState, toState);
    }

    private static boolean isNonAvailableTransition(EntityState fromState, EntityState toState) {
        return isNull(toState) || !isTransitionStateAvailable(fromState, toState);
    }

    private static boolean isTransitionStateAvailable(EntityState fromState, EntityState toState) {
        return isNull(fromState)
                ? getAvailableEntityTransitionStates(TRANSIENT).contains(toState)
                : getAvailableEntityTransitionStates(fromState).contains(toState);
    }
}
