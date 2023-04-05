package com.bobocode.svydovets.bibernate.testdata.entity;

import com.bobocode.svydovets.bibernate.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(value = "child")
public class Child {
    @Id private Long id;

    @Column private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
}
