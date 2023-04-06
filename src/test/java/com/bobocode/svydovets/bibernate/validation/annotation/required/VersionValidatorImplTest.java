package com.bobocode.svydovets.bibernate.validation.annotation.required;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_MORE_THAN_ONE_VERSION;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_UNSUPPORTED_VERSION_TYPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.validation.version.EntityWithTwoVersions;
import com.bobocode.svydovets.bibernate.testdata.entity.validation.version.SupportedVersionTypes;
import com.bobocode.svydovets.bibernate.testdata.entity.validation.version.UnsupportedVersionTypes;
import com.bobocode.svydovets.bibernate.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VersionValidatorImplTest {
    private final Validator versionValidator = new VersionValidatorImpl();

    @Test
    @DisplayName("Validate entity without @Version")
    void validateEntityWithoutIdAnnotation() {
        assertThatNoException().isThrownBy(() -> versionValidator.validate(Person.class));
    }

    @Test
    @DisplayName("More than one field annotated with @Version")
    void validateEntityWithoutNoArgConstructor() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> versionValidator.validate(EntityWithTwoVersions.class))
                .withMessage(
                        String.format(
                                CLASS_HAS_MORE_THAN_ONE_VERSION, EntityWithTwoVersions.class.getName(), 2));
    }

    @Test
    @DisplayName("Entity with supported type for @Version field should be processed")
    void entityWithSupportedIdTypeShouldBeProcessed() {
        assertThatNoException()
                .isThrownBy(() -> versionValidator.validate(SupportedVersionTypes.ShortVersion.class));
        assertThatNoException()
                .isThrownBy(() -> versionValidator.validate(SupportedVersionTypes.IntegerVersion.class));
        assertThatNoException()
                .isThrownBy(() -> versionValidator.validate(SupportedVersionTypes.LongVersion.class));
        assertThatNoException()
                .isThrownBy(
                        () -> versionValidator.validate(SupportedVersionTypes.PrimitiveShortVersion.class));
        assertThatNoException()
                .isThrownBy(
                        () -> versionValidator.validate(SupportedVersionTypes.PrimitiveIntegerVersion.class));
        assertThatNoException()
                .isThrownBy(
                        () -> versionValidator.validate(SupportedVersionTypes.PrimitiveLongVersion.class));
    }

    @Test
    @DisplayName("Unsupported data type of @Version field")
    void entityWithUnsupportedIdTypeShouldNotBeProcessed() {
        assertThrowUnsupportedVersion(UnsupportedVersionTypes.ObjectVersion.class, Object.class);
        assertThrowUnsupportedVersion(UnsupportedVersionTypes.ByteVersion.class, Byte.class);
        assertThrowUnsupportedVersion(UnsupportedVersionTypes.FloatVersion.class, Float.class);
        assertThrowUnsupportedVersion(UnsupportedVersionTypes.DoubleVersion.class, Double.class);
        assertThrowUnsupportedVersion(UnsupportedVersionTypes.CharacterVersion.class, Character.class);
        assertThrowUnsupportedVersion(UnsupportedVersionTypes.BooleanVersion.class, Boolean.class);
        assertThrowUnsupportedVersion(UnsupportedVersionTypes.PrimitiveByteVersion.class, byte.class);
        assertThrowUnsupportedVersion(UnsupportedVersionTypes.PrimitiveFloatVersion.class, float.class);
        assertThrowUnsupportedVersion(
                UnsupportedVersionTypes.PrimitiveDoubleVersion.class, double.class);
        assertThrowUnsupportedVersion(
                UnsupportedVersionTypes.PrimitiveCharacterVersion.class, char.class);
        assertThrowUnsupportedVersion(
                UnsupportedVersionTypes.PrimitiveBooleanVersion.class, boolean.class);
        assertThrowUnsupportedVersion(
                UnsupportedVersionTypes.CustomClassVersion.class,
                UnsupportedVersionTypes.CustomClass.class);
    }

    private void assertThrowUnsupportedVersion(Class<?> entityType, Class<?> idType) {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> versionValidator.validate(entityType))
                .withMessage(
                        String.format(
                                CLASS_HAS_UNSUPPORTED_VERSION_TYPE, entityType.getName(), idType.getName()));
    }
}
