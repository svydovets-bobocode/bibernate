package com.bobocode.svydovets.bibernate.testdata.entity;

import com.bobocode.svydovets.bibernate.annotation.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(value = "parent")
public class Parent {
    @Id private Long id;

    @Column private String name;

    @OneToMany private List<Child> children = new ArrayList<>();
}
