package com.bobocode.svydovets.bibernate.testdata.entity;

import com.bobocode.svydovets.bibernate.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {
    private int id;
    private String name;
}
