package com.bobocode.svydovets.bibernate.action.query;

import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveIdColumnName;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveTableName;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public
class SqlQueryBuilder { // Todo: transform it to a abstract class or interface to cover different
    // dialects
    // Todo: mb create enum to store queries
    private static final String SELECT_FROM_TABLE_BY_ID_PARAM = "SELECT * FROM %s WHERE %s = ?;";
    private static final String SELECT_ALL_FROM_TABLE = "SELECT * FROM %s;";

    public String createSelectByIdQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        String idColumnName = resolveIdColumnName(entityType);
        return String.format(SELECT_FROM_TABLE_BY_ID_PARAM, tableName, idColumnName);
    }

    public String createSelectAllQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        return String.format(SELECT_ALL_FROM_TABLE, tableName);
    }
}
