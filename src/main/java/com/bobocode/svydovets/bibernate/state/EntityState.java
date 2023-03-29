package com.bobocode.svydovets.bibernate.state;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum EntityState {
    TRANSIENT,
    MANAGED,
    REMOVED,
    DETACHED;
}
