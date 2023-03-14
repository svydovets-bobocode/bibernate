package com.bobocode.svydovets.bibernate.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.util.entities.EntityWithoutNonArgConstructor;
import com.bobocode.svydovets.bibernate.util.entities.NonEntityClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityUtilsTest {
    @Test
    @DisplayName("Non-entity class validation")
    void nonEntityClassValidation() {
        EntityValidationException exception =
                assertThrows(
                        EntityValidationException.class,
                        () -> EntityUtils.validateEntity(NonEntityClass.class));
        assertThat(exception.getMessage())
                .isEqualTo(
                        "Class 'com.bobocode.svydovets.bibernate.util.entities.NonEntityClass' has no @Entity annotation (every entity class must be annotated with '@Entity')");
    }

    @Test
    @DisplayName("Validate entity without no-arg constructor")
    void validateEntityWithoutNoArgConstructor() {
        EntityValidationException exception =
                assertThrows(
                        EntityValidationException.class,
                        () -> EntityUtils.validateEntity(EntityWithoutNonArgConstructor.class));
        assertThat(exception.getMessage())
                .isEqualTo(
                        "Entity 'com.bobocode.svydovets.bibernate.util.entities.EntityWithoutNonArgConstructor' has no 'no-arg constructor' (every '@Entity' class must declare 'no-arg constructor')");
    }
}
