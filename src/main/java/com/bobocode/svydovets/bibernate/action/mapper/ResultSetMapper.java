package com.bobocode.svydovets.bibernate.action.mapper;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public class ResultSetMapper {

    @SneakyThrows // Todo: replace with custom exceptions
    public static <T> T mapToObject(Class<T> type, ResultSet resultSet) {
        T instance = type.getDeclaredConstructor().newInstance();
        while (resultSet.next()) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                // TODO: Handle annotations and field types
                Object fieldValue = resultSet.getObject(field.getName(), field.getType());
                field.setAccessible(true);
                field.set(instance, fieldValue);
            }
        }
        return instance;
    }
}
