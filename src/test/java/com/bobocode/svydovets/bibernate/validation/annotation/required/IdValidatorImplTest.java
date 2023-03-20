package com.bobocode.svydovets.bibernate.validation.annotation.required;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ID;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutIdAnnotation;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutNonArgConstructor;
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
}
