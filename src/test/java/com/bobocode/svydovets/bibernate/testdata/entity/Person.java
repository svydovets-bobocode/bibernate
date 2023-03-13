package com.bobocode.svydovets.bibernate.testdata.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Person {
    private Long id;
    private String firstName;
    private String lastName;
}
