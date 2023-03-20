package com.bobocode.svydovets.bibernate.validation.annotation.required.processor;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ENTITY_ANNOTATION;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ID;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutIdAnnotation;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutNonArgConstructor;
import com.bobocode.svydovets.bibernate.testdata.entity.NonEntityClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequiredAnnotationValidatorProcessorImplTest {

    private final RequiredAnnotationValidatorProcessor validatorProcessor =
            new RequiredAnnotationValidatorProcessorImpl();

    @Test
    @DisplayName("Non-entity class validation")
    void nonEntityClassValidation() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> validatorProcessor.validate(NonEntityClass.class, Operation.SELECT))
                .withMessage(String.format(CLASS_HAS_NO_ENTITY_ANNOTATION, NonEntityClass.class.getName()));
    }

    @Test
    @DisplayName("Non-entity class validation using all required annotation validators")
    void nonEntityClassValidationUsingAllRequiredValidators() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> validatorProcessor.validate(NonEntityClass.class))
                .withMessage(String.format(CLASS_HAS_NO_ENTITY_ANNOTATION, NonEntityClass.class.getName()));
    }

    @Test
    @DisplayName("Validate entity without no-arg constructor")
    void validateEntityWithoutNoArgConstructor() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(
                        () ->
                                validatorProcessor.validate(EntityWithoutNonArgConstructor.class, Operation.SELECT))
                .withMessage(
                        String.format(
                                CLASS_HAS_NO_ARG_CONSTRUCTOR, EntityWithoutNonArgConstructor.class.getName()));
    }

    @Test
    @DisplayName("Validate entity without @Id")
    void validateEntityWithoutIdAnnotation() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(
                        () -> validatorProcessor.validate(EntityWithoutIdAnnotation.class, Operation.SELECT))
                .withMessage(String.format(CLASS_HAS_NO_ID, EntityWithoutIdAnnotation.class.getName()));
    }
}
