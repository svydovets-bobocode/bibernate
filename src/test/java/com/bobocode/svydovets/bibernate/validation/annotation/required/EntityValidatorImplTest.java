package com.bobocode.svydovets.bibernate.validation.annotation.required;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ENTITY_ANNOTATION;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutNonArgConstructor;
import com.bobocode.svydovets.bibernate.testdata.entity.NonEntityClass;
import com.bobocode.svydovets.bibernate.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityValidatorImplTest {

    private final Validator entityValidator = new EntityValidatorImpl();

    @Test
    @DisplayName("Non-entity class validation using EntityValidatorImpl")
    void nonEntityClassValidation() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> entityValidator.validate(NonEntityClass.class))
                .withMessage(String.format(CLASS_HAS_NO_ENTITY_ANNOTATION, NonEntityClass.class.getName()));
    }

    @Test
    @DisplayName("Validate entity without no-arg constructor using EntityValidatorImpl")
    void validateEntityWithoutNoArgConstructor() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> entityValidator.validate(EntityWithoutNonArgConstructor.class))
                .withMessage(
                        String.format(
                                CLASS_HAS_NO_ARG_CONSTRUCTOR, EntityWithoutNonArgConstructor.class.getName()));
    }
}
