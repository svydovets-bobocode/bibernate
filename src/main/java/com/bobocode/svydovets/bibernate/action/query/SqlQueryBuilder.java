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

/**
 * The {@code SqlQueryBuilder} class provides a set of static methods for building SQL queries based
 * on the passed entity type and its properties. This class includes methods for building SELECT,
 * INSERT, UPDATE, and DELETE queries with additional options such as locking and versioning.
 */
@Slf4j
public class SqlQueryBuilder {
    private static final String SELECT_FROM_TABLE_BY_ID = "SELECT * FROM %s WHERE %s = ? %s;";
    private static final String SELECT_ALL_FROM_TABLE = "SELECT * FROM %s;";
    private static final String INSERT_INTO_TABLE = "INSERT INTO %s(%s) VALUES(%s);";

    private static final String UPDATE_TABLE = "UPDATE %s SET %s WHERE %s;";
    private static final String DELETE_FROM_TABLE_BY_ID = "DELETE FROM %s WHERE %s = ?;";

    private static final String VERSION_WHERE_CONDITION = " AND %s = ?;";

    private SqlQueryBuilder() {
        throw new BibernateException("Utility SqlQueryBuilder should not be instantiated");
    }

    /**
     * Builds a SELECT query with a WHERE clause that selects an entity by its ID.
     *
     * @param entityType the entity type to build the query for.
     * @return a SELECT query with a WHERE clause that selects an entity by its ID.
     */
    public static String createSelectByIdQuery(Class<?> entityType) {
        return createSelectByIdQuery(entityType, LockModeType.NONE);
    }

    /**
     * Builds a SELECT query with a WHERE clause that selects an entity by its ID and applies a
     * specified lock mode.
     *
     * @param entityType the entity type to build the query for.
     * @param lockModeType the lock mode to apply to the query.
     * @return a SELECT query with a WHERE clause that selects an entity by its ID and applies a
     *     specified lock mode.
     */
    public static String createSelectByIdQuery(Class<?> entityType, LockModeType lockModeType) {
        String tableName = resolveTableName(entityType);
        String idColumnName = resolveIdColumnName(entityType);
        String lockMode = lockModeType.getValue();
        return String.format(SELECT_FROM_TABLE_BY_ID, tableName, idColumnName, lockMode);
    }

    /**
     * Builds a SELECT query that retrieves all entities from the specified table.
     *
     * @param entityType the entity type to build the query for.
     * @return a SELECT query that retrieves all entities from the specified table.
     */
    public static String createSelectAllQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        return String.format(SELECT_ALL_FROM_TABLE, tableName);
    }

    /**
     * Builds a DELETE query with a WHERE clause that deletes an entity by its ID.
     *
     * @param entityType the entity type to build the query for.
     * @return a DELETE query with a WHERE clause that deletes an entity by its ID.
     */
    public static String createDeleteByIdQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        String idColumnName = resolveIdColumnName(entityType);
        return String.format(DELETE_FROM_TABLE_BY_ID, tableName, idColumnName);
    }

    /**
     * Retrieves a list of column names for fields that are updatable in the specified entity class.
     *
     * @param entityType The entity class for which to retrieve updatable fields.
     * @return A list of column names for fields that are updatable in the specified entity class.
     */
    private static List<String> getColumnNamesForUpdate(Class<?> entityType) {
        List<String> columnNames = new ArrayList<>();
        Field[] insertableFields = getUpdatableFields(entityType);
        for (Field declaredField : insertableFields) {
            var columnName = resolveColumnName(declaredField);
            columnNames.add(columnName);
        }
        return columnNames;
    }

    /**
     * Retrieves a list of column names for fields that are insertable in the specified entity class.
     *
     * @param entityType The entity class for which to retrieve insertable fields.
     * @return A list of column names for fields that are insertable in the specified entity class.
     */
    private static List<String> getColumnNamesForInsert(Class<?> entityType) {
        List<String> columnNames = new ArrayList<>();
        Field[] insertableFields = getInsertableFields(entityType);
        for (Field declaredField : insertableFields) {
            var columnName = resolveColumnName(declaredField);
            columnNames.add(columnName);
        }
        return columnNames;
    }

    /**
     * Creates an SQL insert query based on the specified entity class.
     *
     * @param entityType The entity class for which to create an SQL insert query.
     * @return An SQL insert query based on the specified entity class.
     */
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

    /**
     * Creates an SQL update query based on the specified entity class.
     *
     * @param entityType The entity class for which to create an SQL update query.
     * @return An SQL update query based on the specified entity class.
     */
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

    /**
     * Adds a version field to the WHERE condition of the specified SQL query if necessary.
     *
     * @param originSqlQuery The original SQL query.
     * @param entityType The entity class associated with the SQL query.
     * @return The modified SQL query with a version field in the WHERE condition if necessary.
     */
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
