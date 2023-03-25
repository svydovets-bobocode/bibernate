package com.bobocode.svydovets.bibernate.testdata.entity.validation;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;

@Entity
public class EntityWithTwoId {
    @Id private Integer firstId;
    @Id private Integer secondId;
}
