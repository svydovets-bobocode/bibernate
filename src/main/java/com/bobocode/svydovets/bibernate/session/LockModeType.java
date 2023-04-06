package com.bobocode.svydovets.bibernate.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LockModeType {
    NONE(""),
    FOR_SHARE("FOR SHARE"),
    FOR_UPDATE("FOR UPDATE");

    @Getter private final String value;
}
