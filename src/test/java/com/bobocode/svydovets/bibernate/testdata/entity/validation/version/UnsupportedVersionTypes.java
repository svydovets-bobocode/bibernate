package com.bobocode.svydovets.bibernate.testdata.entity.validation.version;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.annotation.Version;

public class UnsupportedVersionTypes {

    @Entity
    public static class ObjectVersion {
        @Id private Integer id;
        @Version private Object version;
    }

    @Entity
    public static class ByteVersion {
        @Id private Integer id;
        @Version private Byte version;
    }

    @Entity
    public static class FloatVersion {
        @Id private Integer id;
        @Version private Float version;
    }

    @Entity
    public static class DoubleVersion {
        @Id private Integer id;
        @Version private Double version;
    }

    @Entity
    public static class CharacterVersion {
        @Id private Integer id;
        @Version private Character version;
    }

    @Entity
    public static class BooleanVersion {
        @Id private Integer id;
        @Version private Boolean version;
    }

    @Entity
    public static class PrimitiveByteVersion {
        @Id private Integer id;
        @Version private byte version;
    }

    @Entity
    public static class PrimitiveFloatVersion {
        @Id private Integer id;
        @Version private float version;
    }

    @Entity
    public static class PrimitiveDoubleVersion {
        @Id private Integer id;
        @Version private double version;
    }

    @Entity
    public static class PrimitiveCharacterVersion {
        @Id private Integer id;
        @Version private char version;
    }

    @Entity
    public static class PrimitiveBooleanVersion {
        @Id private Integer id;
        @Version private boolean version;
    }

    @Entity
    public static class CustomClassVersion {
        @Id private Integer id;
        @Version private CustomClass version;
    }

    public static class CustomClass {}
}
