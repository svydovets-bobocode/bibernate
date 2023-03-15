package com.bobocode.svydovets.bibernate.testdata.entity;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Person {
    private Long id;
    private String firstName;
    private String lastName;
}
