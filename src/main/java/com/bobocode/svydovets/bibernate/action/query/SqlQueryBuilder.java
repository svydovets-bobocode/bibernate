package com.bobocode.svydovets.bibernate.action.query;

import static com.bobocode.svydovets.bibernate.util.EntityUtils.getInsertableFields;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveColumnName;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveIdColumnName;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveTableName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public
class SqlQueryBuilder { // Todo: transform it to a abstract class or interface to cover different
    // dialects
    // Todo: mb create enum to store queries
    private static final String SELECT_FROM_TABLE_BY_ID_PARAM = "SELECT * FROM %s WHERE %s = ?;";
    private static final String SELECT_ALL_FROM_TABLE = "SELECT * FROM %s;";
    private static final String INSERT_INTO_TABLE = "INSERT INTO %s(%s) VALUES(%s);";

    public static String createSelectByIdQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        String idColumnName = resolveIdColumnName(entityType);
        return String.format(SELECT_FROM_TABLE_BY_ID_PARAM, tableName, idColumnName);
    }

    public static String createSelectAllQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        return String.format(SELECT_ALL_FROM_TABLE, tableName);
    }

    public static String createInsertQuery(Class<?> entityType) {
        var tableName = resolveTableName(entityType);
        List<String> columnNames = new ArrayList<>();
        List<String> valuePlaceholder = new ArrayList<>();
        Field[] insertableFields = getInsertableFields(entityType);
        for (Field declaredField : insertableFields) {
            var columnName = resolveColumnName(declaredField);
            columnNames.add(columnName);
            valuePlaceholder.add("?");
        }

        return String.format(
                INSERT_INTO_TABLE,
                tableName,
                String.join(",", columnNames),
                String.join(",", valuePlaceholder));
    }
}
