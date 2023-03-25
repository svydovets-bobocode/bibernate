package com.bobocode.svydovets.bibernate.testdata.entity.validation;

import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;

public class UnsupportedIdTypes {

    @Entity
    public static class ObjectId {
        @Id private Object id;
    }

    @Entity
    public static class ByteId {
        @Id private Byte id;
    }

    @Entity
    public static class ShortId {
        @Id private Short id;
    }

    @Entity
    public static class FloatId {
        @Id private Float id;
    }

    @Entity
    public static class DoubleId {
        @Id private Double id;
    }

    @Entity
    public static class CharacterId {
        @Id private Character id;
    }

    @Entity
    public static class BooleanId {
        @Id private Boolean id;
    }

    @Entity
    public static class PrimitiveByteId {
        @Id private byte id;
    }

    @Entity
    public static class PrimitiveShortId {
        @Id private short id;
    }

    @Entity
    public static class PrimitiveFloatId {
        @Id private float id;
    }

    @Entity
    public static class PrimitiveDoubleId {
        @Id private double id;
    }

    @Entity
    public static class PrimitiveCharId {
        @Id private char id;
    }

    @Entity
    public static class PrimitiveBooleanId {
        @Id private boolean id;
    }

    @Entity
    public static class CustomClassId {
        @Id private CustomClass id;
    }

    public static class CustomClass {}
}
