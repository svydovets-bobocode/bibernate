package com.bobocode.svydovets.bibernate.validation;

import com.bobocode.svydovets.bibernate.state.EntityState;

public interface EntityStateValidator {

    void validate(EntityState fromState, EntityState toState);
}
