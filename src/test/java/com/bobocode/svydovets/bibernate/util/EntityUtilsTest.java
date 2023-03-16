package com.bobocode.svydovets.bibernate.util;

import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ARG_CONSTRUCTOR;
import static com.bobocode.svydovets.bibernate.util.Constants.CLASS_HAS_NO_ENTITY_ANNOTATION;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.EntityWithoutNonArgConstructor;
import com.bobocode.svydovets.bibernate.testdata.entity.NonEntityClass;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import java.lang.reflect.Field;
import java.util.stream.Stream;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class EntityUtilsTest {
    @Nested
    @Order(1)
    @DisplayName("1. @Entity test")
    class EntityAnnotationTest {
        @Test
        @DisplayName("Non-entity class validation")
        void nonEntityClassValidation() {
            assertThatExceptionOfType(EntityValidationException.class)
                    .isThrownBy(() -> EntityUtils.validateEntity(NonEntityClass.class))
                    .withMessage(
                            String.format(CLASS_HAS_NO_ENTITY_ANNOTATION, NonEntityClass.class.getName()));
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

    @Nested
    @Order(2)
    @DisplayName("2. @Column test")
    class ColumnAnnotationTest {
        @ParameterizedTest
        @MethodSource("entityWithFieldNamesSource")
        @DisplayName("Resolve column name")
        void resolveColumnName(Class<?> entityClass, String fieldName, String expected)
                throws NoSuchFieldException {
            var result = EntityUtils.resolveColumnName(entityClass.getDeclaredField(fieldName));
            assertEquals(expected, result);
        }

        private static Stream<Arguments> entityWithFieldNamesSource() {
            return Stream.of(
                    Arguments.of(Person.class, "firstName", "firstName"),
                    Arguments.of(Person.class, "lastName", "last_name"));
        }

        @ParameterizedTest
        @MethodSource("entityWithInsertableFieldSource")
        @DisplayName("Get insertable fields")
        void getInsertableFields(Class<?> entityClass, Field[] expected) {
            var result = EntityUtils.getInsertableFields(entityClass);
            assertArrayEquals(expected, result);
        }

        private static Stream<Arguments> entityWithInsertableFieldSource() throws NoSuchFieldException {
            return Stream.of(
                    Arguments.of(Person.class, new Field[] {Person.class.getDeclaredField("firstName")}),
                    Arguments.of(
                            User.class,
                            new Field[] {
                                User.class.getDeclaredField("name"), User.class.getDeclaredField("phone")
                            }));
        }

        @ParameterizedTest
        @MethodSource("entityWithUpdatableFieldSource")
        @DisplayName("Get updatable fields")
        void getUpdatableFields(Class<?> entityClass, Field[] expected) {
            var result = EntityUtils.getUpdatableFields(entityClass);
            assertArrayEquals(expected, result);
        }

        private static Stream<Arguments> entityWithUpdatableFieldSource() throws NoSuchFieldException {
            return Stream.of(
                    Arguments.of(Person.class, new Field[] {Person.class.getDeclaredField("lastName")}),
                    Arguments.of(
                            User.class,
                            new Field[] {
                                User.class.getDeclaredField("name"), User.class.getDeclaredField("phone")
                            }));
        }
    }
}
