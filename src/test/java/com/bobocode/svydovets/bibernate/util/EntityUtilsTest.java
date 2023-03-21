package com.bobocode.svydovets.bibernate.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import java.lang.reflect.Field;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class EntityUtilsTest {
    // todo: make sure it does not exists and make it work
    //    @Nested
    //    @Order(1)
    //    @DisplayName("1. @Entity test")
    //    class EntityAnnotationTest {
    //        @Test
    //        @DisplayName("Non-entity class validation")
    //        void nonEntityClassValidation() {
    //            assertThatExceptionOfType(EntityValidationException.class)
    //                    .isThrownBy(() -> EntityUtils.validateEntity(NonEntityClass.class))
    //                    .withMessage(
    //                            String.format(CLASS_HAS_NO_ENTITY_ANNOTATION,
    // NonEntityClass.class.getName()));
    //        }
    //
    //        @Test
    //        @DisplayName("Validate entity without no-arg constructor")
    //        void validateEntityWithoutNoArgConstructor() {
    //            assertThatExceptionOfType(EntityValidationException.class)
    //                    .isThrownBy(() ->
    // EntityUtils.validateEntity(EntityWithoutNonArgConstructor.class))
    //                    .withMessage(
    //                            String.format(
    //                                    CLASS_HAS_NO_ARG_CONSTRUCTOR,
    // EntityWithoutNonArgConstructor.class.getName()));
    //        }
    //    }

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
                    Arguments.of(Person.class, "firstName", "first_name"),
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

    // todo: change EntityUtils.validateEntity to Validators, move to IdValidatorImplTest.java
    //    @Nested
    //    @Order(3)
    //    @DisplayName("3. @Id test")
    //    class IdAnnotationTest {
    //        @Test
    //        @DisplayName("Entity with valid @Id field should be processed")
    //        void entityWithValidIdShouldBeProcessed() {
    //            assertThatNoException().isThrownBy(() -> EntityUtils.validateEntity(Person.class));
    //        }
    //
    //        @Test
    //        @DisplayName("No fields annotated with @Id")
    //        void noFieldsAnnotatedWithId() {
    //            assertThatExceptionOfType(EntityValidationException.class)
    //                    .isThrownBy(() -> EntityUtils.validateEntity(EntityWithoutId.class))
    //                    .withMessage(String.format(CLASS_HAS_NO_ID,
    // EntityWithoutId.class.getName()));
    //        }
    //
    //        @Test
    //        @DisplayName("More than one field annotated with @Id")
    //        void moreThanOneFieldsAnnotatedWithId() {
    //            assertThatExceptionOfType(EntityValidationException.class)
    //                    .isThrownBy(() -> EntityUtils.validateEntity(EntityWithTwoId.class))
    //                    .withMessage(
    //                            String.format(CLASS_HAS_MORE_THAN_ONE_ID,
    // EntityWithTwoId.class.getName(), 2));
    //        }
    //
    //        @Test
    //        @DisplayName("Entity with supported type for @Id field should be processed")
    //        void entityWithSupportedIdTypeShouldBeProcessed() {
    //            assertThatNoException()
    //                    .isThrownBy(
    //                            () -> EntityUtils.validateEntity(SupportedIdTypes.IntegerId.class));
    // // Integer
    //            assertThatNoException()
    //                    .isThrownBy(() ->
    // EntityUtils.validateEntity(SupportedIdTypes.LongId.class)); // Long
    //            assertThatNoException()
    //                    .isThrownBy(() ->
    // EntityUtils.validateEntity(SupportedIdTypes.UuidId.class)); // UUID
    //            assertThatNoException()
    //                    .isThrownBy(() ->
    // EntityUtils.validateEntity(SupportedIdTypes.StringId.class)); // String
    //            assertThatNoException()
    //                    .isThrownBy(
    //                            () ->
    // EntityUtils.validateEntity(SupportedIdTypes.BigDecimalId.class)); // BigDecimal
    //            assertThatNoException()
    //                    .isThrownBy(
    //                            () ->
    // EntityUtils.validateEntity(SupportedIdTypes.BigIntegerId.class)); // BigInteger
    //            assertThatNoException()
    //                    .isThrownBy(
    //                            () ->
    //
    // EntityUtils.validateEntity(SupportedIdTypes.DateUtilId.class)); // java.util.Date
    //            assertThatNoException()
    //                    .isThrownBy(
    //                            () -> EntityUtils.validateEntity(SupportedIdTypes.DateSqlId.class));
    // // java.sql.Date
    //            assertThatNoException()
    //                    .isThrownBy(
    //                            () ->
    // EntityUtils.validateEntity(SupportedIdTypes.PrimitiveIntId.class)); // int
    //            assertThatNoException()
    //                    .isThrownBy(
    //                            () ->
    // EntityUtils.validateEntity(SupportedIdTypes.PrimitiveLongId.class)); // long
    //        }
    //
    //        @Test
    //        @DisplayName("Unsupported data type of @Id column")
    //        void entityWithUnsupportedIdTypeShouldNotBeProcessed() {
    //            assertThrowUnsupportedId(UnsupportedIdTypes.ObjectId.class, Object.class); // Object
    //            assertThrowUnsupportedId(UnsupportedIdTypes.ByteId.class, Byte.class); // Byte
    //            assertThrowUnsupportedId(UnsupportedIdTypes.ShortId.class, Short.class); // Short
    //            assertThrowUnsupportedId(UnsupportedIdTypes.FloatId.class, Float.class); // Float
    //            assertThrowUnsupportedId(UnsupportedIdTypes.DoubleId.class, Double.class); // Double
    //            assertThrowUnsupportedId(UnsupportedIdTypes.CharacterId.class, Character.class); //
    // Character
    //            assertThrowUnsupportedId(UnsupportedIdTypes.BooleanId.class, Boolean.class); //
    // Boolean
    //            assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveByteId.class, byte.class); //
    // byte
    //            assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveShortId.class, short.class); //
    // short
    //            assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveFloatId.class, float.class); //
    // float
    //            assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveDoubleId.class, double.class);
    // // double
    //            assertThrowUnsupportedId(UnsupportedIdTypes.PrimitiveCharId.class, char.class); //
    // char
    //            assertThrowUnsupportedId(
    //                    UnsupportedIdTypes.PrimitiveBooleanId.class, boolean.class); // boolean
    //            assertThrowUnsupportedId(
    //                    UnsupportedIdTypes.CustomClassId.class,
    //                    UnsupportedIdTypes.CustomClass.class); // CustomClass
    //        }
    //
    //        private void assertThrowUnsupportedId(Class<?> entityType, Class<?> idType) {
    //            assertThatExceptionOfType(EntityValidationException.class)
    //                    .isThrownBy(() -> EntityUtils.validateEntity(entityType))
    //                    .withMessage(
    //                            String.format(CLASS_HAS_UNSUPPORTED_ID_TYPE, entityType.getName(),
    // idType.getName()));
    //        }
    //    }
}
