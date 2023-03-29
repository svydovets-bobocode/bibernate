package com.bobocode.svydovets.bibernate.validation.state;

import static com.bobocode.svydovets.bibernate.state.EntityState.DETACHED;
import static com.bobocode.svydovets.bibernate.state.EntityState.MANAGED;
import static com.bobocode.svydovets.bibernate.state.EntityState.REMOVED;
import static com.bobocode.svydovets.bibernate.state.EntityState.TRANSIENT;
import static java.util.stream.Collectors.toMap;

import com.bobocode.svydovets.bibernate.state.EntityState;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EntityStateTransition {
    TRANSIENT_TRANSITION(TRANSIENT, Set.of(MANAGED)),
    MANAGED_TRANSITION(MANAGED, Set.of(MANAGED, DETACHED, REMOVED)),
    REMOVED_TRANSITION(REMOVED, Set.of(MANAGED)),
    DETACHED_TRANSITION(DETACHED, Set.of(MANAGED));

    EntityState fromState;
    Set<EntityState> toEntityStates;

    private static final Map<EntityState, Set<EntityState>> TRANSITION_STATE = initTransitionState();

    private static Map<EntityState, Set<EntityState>> initTransitionState() {
        return Stream.of(EntityStateTransition.values())
                .collect(
                        toMap(EntityStateTransition::getFromState, EntityStateTransition::getToEntityStates));
    }

    public static Set<EntityState> getAvailableEntityTransitionStates(EntityState entityState) {
        return TRANSITION_STATE.get(entityState);
    }
}
