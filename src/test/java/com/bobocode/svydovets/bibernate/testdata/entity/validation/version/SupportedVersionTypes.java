package com.bobocode.svydovets.bibernate.testdata.entity.validation.version;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.annotation.Version;

public class SupportedVersionTypes {
    @Entity
    public static class ShortVersion {
        @Id private Integer id;
        @Version private Short version;
    }

    @Entity
    public static class IntegerVersion {
        @Id private Integer id;
        @Version private Integer version;
    }

    @Entity
    public static class LongVersion {
        @Id private Integer id;
        @Version private Long version;
    }

    @Entity
    public static class PrimitiveShortVersion {
        @Id private Integer id;
        @Version private short version;
    }

    @Entity
    public static class PrimitiveIntegerVersion {
        @Id private Integer id;
        @Version private int version;
    }

    @Entity
    public static class PrimitiveLongVersion {
        @Id private Integer id;
        @Version private long version;
    }
}
