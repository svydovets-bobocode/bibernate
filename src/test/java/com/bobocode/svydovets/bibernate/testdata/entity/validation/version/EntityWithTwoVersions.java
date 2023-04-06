package com.bobocode.svydovets.bibernate.testdata.entity.validation.version;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.annotation.Version;

@Entity
public class EntityWithTwoVersions {
    @Id private Integer id;
    @Version private Integer firstVersion;

    @Version private Integer secondVersion;
}
