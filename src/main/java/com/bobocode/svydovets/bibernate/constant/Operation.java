package com.bobocode.svydovets.bibernate.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operation {
    SELECT,
    UPDATE,
    INSERT,
    DELETE
}
