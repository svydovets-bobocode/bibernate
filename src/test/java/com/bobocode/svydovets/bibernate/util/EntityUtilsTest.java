package com.bobocode.svydovets.bibernate.util;

import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ENTITY_ANNOTATION;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutNonArgConstructor;
import com.bobocode.svydovets.bibernate.testdata.entity.NonEntityClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityUtilsTest {
    @Test
    @DisplayName("Non-entity class validation")
    void nonEntityClassValidation() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> EntityUtils.validateEntity(NonEntityClass.class))
                .withMessage(String.format(CLASS_HAS_NO_ENTITY_ANNOTATION, NonEntityClass.class.getName()));
    }

    @Test
    @DisplayName("Validate entity without no-arg constructor")
    void validateEntityWithoutNoArgConstructor() {
        assertThatExceptionOfType(EntityValidationException.class)
                .isThrownBy(() -> EntityUtils.validateEntity(EntityWithoutNonArgConstructor.class))
                .withMessage(
                        String.format(
                                CLASS_HAS_NO_ARG_CONSTRUCTOR, EntityWithoutNonArgConstructor.class.getName()));
    }
}
