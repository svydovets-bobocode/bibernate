package com.bobocode.svydovets.bibernate.util.entities;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Table;

@Table("")
@Entity
public class EntityWithoutNonArgConstructor {
    public EntityWithoutNonArgConstructor(int i) {}
}
