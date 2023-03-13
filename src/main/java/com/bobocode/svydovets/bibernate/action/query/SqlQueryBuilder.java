package com.bobocode.svydovets.bibernate.action.query;

import com.bobocode.svydovets.bibernate.annotation.Table;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlQueryBuilder { // Todo: transform it to a abstract class or interface to cover different dialects
    // Todo: id can have different name
    // Todo: mb create enum to store queries
    private static final String SELECT_FROM_TABLE_BY_ID_PARAM = "SELECT * FROM %s WHERE id = ?;";

    public String createSelectByIdQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        return String.format(SELECT_FROM_TABLE_BY_ID_PARAM, tableName);
    }

    // Todo: tableName resolver
    private String resolveTableName(Class<?> entityType) {
        log.trace("Resolving table name for entity {}", entityType);
        String tableName;
        if (entityType.isAnnotationPresent(Table.class)) {
            tableName = entityType.getDeclaredAnnotation(Table.class).value();
            log.trace("Table is specified explicitly as {}", tableName);
        } else {
            tableName = entityType.getSimpleName().toLowerCase();
            log.trace("Table is explicitly specified, falling back to call name {}", tableName);
        }
        return tableName;
    }
}
