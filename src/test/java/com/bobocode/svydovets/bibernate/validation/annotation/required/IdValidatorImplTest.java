package com.bobocode.svydovets.bibernate.validation.annotation.required;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_MORE_THAN_ONE_ID;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ID;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_UNSUPPORTED_ID_TYPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithTwoId;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutIdAnnotation;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutNonArgConstructor;
import com.bobocode.svydovets.bibernate.testdata.entity.SupportedIdTypes;
import com.bobocode.svydovets.bibernate.testdata.entity.UnsupportedIdTypes;
import com.bobocode.svydovets.bibernate.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IdValidatorImplTest {

    private final Validator idValidator = new IdValidatorImpl();

    @Test
    @DisplayName("Validate entity without @Id")
    void validateEntityWithoutIdAnnotation() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> idValidator.validate(EntityWithoutIdAnnotation.class))
                .withMessage(String.format(CLASS_HAS_NO_ID, EntityWithoutIdAnnotation.class.getName()));
    }

    @Test
    @DisplayName("Validate entity without no-arg constructor")
    void validateEntityWithoutNoArgConstructor() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> idValidator.validate(EntityWithoutNonArgConstructor.class))
                .withMessage(
                        String.format(
                                CLASS_HAS_NO_ARG_CONSTRUCTOR, EntityWithoutNonArgConstructor.class.getName()));
    }

    @Test
    @DisplayName("More than one field annotated with @Id")
    void moreThanOneFieldsAnnotatedWithId() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> idValidator.validate(EntityWithTwoId.class))
                .withMessage(String.format(CLASS_HAS_MORE_THAN_ONE_ID, EntityWithTwoId.class.getName(), 2));
    }

    @Test
    @DisplayName("Entity with supported type for @Id field should be processed")
    void entityWithSupportedIdTypeShouldBeProcessed() {
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.IntegerId.class)); // Integer
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.LongId.class)); // Long
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.UuidId.class)); // UUID
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.StringId.class)); // String
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.BigDecimalId.class)); // BigDecimal
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.BigIntegerId.class)); // BigInteger
        assertThatNoException()
                .isThrownBy(
                        () -> idValidator.validate(SupportedIdTypes.DateUtilId.class)); // java.util.Date
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.DateSqlId.class)); // java.sql.Date
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.PrimitiveIntId.class)); // int
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.PrimitiveLongId.class)); // long
    }

    @Test
    @DisplayName("Unsupported data type of @Id column")
    void entityWithUnsupportedIdTypeShouldNotBeProcessed() {
        assertThrowUnsupportedId(UnsupportedIdTypes.ObjectId.class, Object.class); // Object
        assertThrowUnsupportedId(UnsupportedIdTypes.ByteId.class, Byte.class); // Byte
        assertThrowUnsupportedId(UnsupportedIdTypes.ShortId.class, Short.class); // Short
        assertThrowUnsupportedId(UnsupportedIdTypes.FloatId.class, Float.class); // Float
        assertThrowUnsupportedId(UnsupportedIdTypes.DoubleId.class, Double.class); // Double
        assertThrowUnsupportedId(UnsupportedIdTypes.CharacterId.class, Character.class); // Character
        assertThrowUnsupportedId(UnsupportedIdTypes.BooleanId.class, Boolean.class); // Boolean
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveByteId.class, byte.class); // byte
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveShortId.class, short.class); // short
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveFloatId.class, float.class); // float
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveDoubleId.class, double.class); // double
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveCharId.class, char.class); // char
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveBooleanId.class, boolean.class); // boolean
        assertThrowUnsupportedId(
                UnsupportedIdTypes.CustomClassId.class,
                UnsupportedIdTypes.CustomClass.class); // CustomClass
    }

    private void assertThrowUnsupportedId(Class<?> entityType, Class<?> idType) {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> idValidator.validate(entityType))
                .withMessage(
                        String.format(CLASS_HAS_UNSUPPORTED_ID_TYPE, entityType.getName(), idType.getName()));
    }
}
