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
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.IntegerId.class));
        assertThatNoException().isThrownBy(() -> idValidator.validate(SupportedIdTypes.LongId.class));
        assertThatNoException().isThrownBy(() -> idValidator.validate(SupportedIdTypes.UuidId.class));
        assertThatNoException().isThrownBy(() -> idValidator.validate(SupportedIdTypes.StringId.class));
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.BigDecimalId.class));
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.BigIntegerId.class));
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.DateUtilId.class));
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.DateSqlId.class));
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.PrimitiveIntId.class));
        assertThatNoException()
                .isThrownBy(() -> idValidator.validate(SupportedIdTypes.PrimitiveLongId.class));
    }

    @Test
    @DisplayName("Unsupported data type of @Id column")
    void entityWithUnsupportedIdTypeShouldNotBeProcessed() {
        assertThrowUnsupportedId(UnsupportedIdTypes.ObjectId.class, Object.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.ByteId.class, Byte.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.ShortId.class, Short.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.FloatId.class, Float.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.DoubleId.class, Double.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.CharacterId.class, Character.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.BooleanId.class, Boolean.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveByteId.class, byte.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveShortId.class, short.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveFloatId.class, float.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveDoubleId.class, double.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveCharId.class, char.class);
        assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveBooleanId.class, boolean.class);
        assertThrowUnsupportedId(
                UnsupportedIdTypes.CustomClassId.class, UnsupportedIdTypes.CustomClass.class);
    }

    private void assertThrowUnsupportedId(Class<?> entityType, Class<?> idType) {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> idValidator.validate(entityType))
                .withMessage(
                        String.format(CLASS_HAS_UNSUPPORTED_ID_TYPE, entityType.getName(), idType.getName()));
    }
}
