package com.bobocode.svydovets.bibernate.action.query;

import static com.bobocode.svydovets.bibernate.util.EntityUtils.getInsertableFields;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.getUpdatableFields;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveColumnName;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveIdColumnName;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveTableName;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.session.LockModeType;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlQueryBuilder {
    // Todo: transform it to a abstract class or interface to cover different dialects
    // Todo: mb create enum to store queries
    private static final String SELECT_FROM_TABLE_BY_ID = "SELECT * FROM %s WHERE %s = ? %s;";
    private static final String SELECT_ALL_FROM_TABLE = "SELECT * FROM %s;";
    private static final String INSERT_INTO_TABLE = "INSERT INTO %s(%s) VALUES(%s);";

    private static final String UPDATE_TABLE = "UPDATE %s SET %s WHERE %s;";
    private static final String DELETE_FROM_TABLE_BY_ID = "DELETE FROM %s WHERE %s = ?;";

    private static final String VERSION_WHERE_CONDITION = " AND %s = ?;";

    private SqlQueryBuilder() {
        throw new BibernateException("Utility SqlQueryBuilder should not be instantiated");
    }

    public static String createSelectByIdQuery(Class<?> entityType) {
        return createSelectByIdQuery(entityType, LockModeType.NONE);
    }

    public static String createSelectByIdQuery(Class<?> entityType, LockModeType lockModeType) {
        String tableName = resolveTableName(entityType);
        String idColumnName = resolveIdColumnName(entityType);
        String lockMode = lockModeType.getValue();
        return String.format(SELECT_FROM_TABLE_BY_ID, tableName, idColumnName, lockMode);
    }

    public static String createSelectAllQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        return String.format(SELECT_ALL_FROM_TABLE, tableName);
    }

    public static String createDeleteByIdQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        String idColumnName = resolveIdColumnName(entityType);
        return String.format(DELETE_FROM_TABLE_BY_ID, tableName, idColumnName);
    }

    private static List<String> getColumnNamesForUpdate(Class<?> entityType) {
        List<String> columnNames = new ArrayList<>();
        Field[] insertableFields = getUpdatableFields(entityType);
        for (Field declaredField : insertableFields) {
            var columnName = resolveColumnName(declaredField);
            columnNames.add(columnName);
        }
        return columnNames;
    }

    private static List<String> getColumnNamesForInsert(Class<?> entityType) {
        List<String> columnNames = new ArrayList<>();
        Field[] insertableFields = getInsertableFields(entityType);
        for (Field declaredField : insertableFields) {
            var columnName = resolveColumnName(declaredField);
            columnNames.add(columnName);
        }
        return columnNames;
    }

    public static String createInsertQuery(Class<?> entityType) {
        var tableName = resolveTableName(entityType);
        List<String> columnNames = getColumnNamesForInsert(entityType);
        List<String> valuePlaceholders = new ArrayList<>(Collections.nCopies(columnNames.size(), "?"));

        return String.format(
                INSERT_INTO_TABLE,
                tableName,
                String.join(",", columnNames),
                String.join(",", valuePlaceholders));
    }

    public static String createUpdateQuery(Class<?> entityType) {
        var tableName = resolveTableName(entityType);
        List<String> columnNames = getColumnNamesForUpdate(entityType);
        List<String> columnValuePairs = new ArrayList<>();

        for (String columnName : columnNames) {
            columnValuePairs.add(columnName + " = ?");
        }

        String whereCondition = "id = ?";
        return String.format(
                UPDATE_TABLE, tableName, String.join(",", columnValuePairs), whereCondition);
    }

    public static String addVersionToWhereConditionIfNeeds(
            String originSqlQuery, Class<?> entityType) {
        return EntityUtils.findVersionField(entityType)
                .flatMap(
                        field ->
                                Optional.of(
                                        originSqlQuery.replace(
                                                ";", String.format(VERSION_WHERE_CONDITION, field.getName()))))
                .orElse(originSqlQuery);
    }
}
