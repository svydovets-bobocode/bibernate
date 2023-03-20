package com.bobocode.svydovets.bibernate.testdata.entity;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Table;

@Table("")
@Entity
public class EntityWithoutIdAnnotation {

    Integer id;

    public EntityWithoutIdAnnotation() {}
}
