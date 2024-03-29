package com.bobocode.svydovets.bibernate.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.testdata.entity.Person;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import com.bobocode.svydovets.bibernate.testdata.entity.validation.ManyToOneWithoutJoinColumn;
import java.lang.reflect.Field;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EntityUtilsTest {

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
                Arguments.of(
                        Person.class,
                        new Field[] {
                            Person.class.getDeclaredField("id"), Person.class.getDeclaredField("firstName")
                        }),
                Arguments.of(
                        User.class,
                        new Field[] {
                            User.class.getDeclaredField("id"),
                            User.class.getDeclaredField("name"),
                            User.class.getDeclaredField("phone")
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

    @ParameterizedTest
    @MethodSource("entityManyToOneWithoutJoinColumn")
    @DisplayName("Check relations configurations")
    void checkRelationsConfiguration(Class<?> entityClass) {
        assertThrows(
                EntityValidationException.class,
                () -> EntityUtils.checkRelationsConfiguration(entityClass));
    }

    private static Stream<Arguments> entityManyToOneWithoutJoinColumn() {
        return Stream.of(Arguments.of(ManyToOneWithoutJoinColumn.class));
    }
}
