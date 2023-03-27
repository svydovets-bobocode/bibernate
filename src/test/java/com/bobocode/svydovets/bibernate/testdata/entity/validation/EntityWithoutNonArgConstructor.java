package com.bobocode.svydovets.bibernate.testdata.entity.validation;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.annotation.Table;

@Table("")
@Entity
public class EntityWithoutNonArgConstructor {
    @Id private Integer id;

    public EntityWithoutNonArgConstructor(int i) {}
}
