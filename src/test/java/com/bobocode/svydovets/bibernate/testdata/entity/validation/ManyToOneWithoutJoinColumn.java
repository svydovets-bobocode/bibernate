package com.bobocode.svydovets.bibernate.testdata.entity.validation;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.annotation.ManyToOne;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;

@Entity
public class ManyToOneWithoutJoinColumn {

    @Id private Long id;

    @ManyToOne private Person person;
}
