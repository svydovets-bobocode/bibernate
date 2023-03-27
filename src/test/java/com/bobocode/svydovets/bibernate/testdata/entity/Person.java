package com.bobocode.svydovets.bibernate.testdata.entity;

import com.bobocode.svydovets.bibernate.annotation.Column;
import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public final class Person {
    @Id private Long id;

    @Column(name = "first_name", updatable = false)
    private String firstName;

    @Column(name = "last_name", insertable = false)
    private String lastName;
}
