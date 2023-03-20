package com.bobocode.svydovets.bibernate.testdata.entity;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

public class SupportedIdTypes {
    @Entity
    public static class IntegerId {
        @Id private Integer id;
    }

    @Entity
    public static class LongId {
        @Id private Long id;
    }

    @Entity
    public static class UuidId {
        @Id private UUID id;
    }

    @Entity
    public static class StringId {
        @Id private String id;
    }

    @Entity
    public static class BigDecimalId {
        @Id private BigDecimal id;
    }

    @Entity
    public static class BigIntegerId {
        @Id private BigInteger id;
    }

    @Entity
    public static class DateUtilId {
        @Id private Date id;
    }

    @Entity
    public static class DateSqlId {
        @Id private java.sql.Date id;
    }

    @Entity
    public static class PrimitiveIntId {
        @Id private int id;
    }

    @Entity
    public static class PrimitiveLongId {
        @Id private long id;
    }
}
