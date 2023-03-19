package com.bobocode.svydovets.bibernate.action.query;

import com.bobocode.svydovets.bibernate.annotation.Table;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public
class SqlQueryBuilder { // Todo: transform it to a abstract class or interface to cover different
    // dialects
    // Todo: mb create enum to store queries
    private static final String SELECT_FROM_TABLE_BY_ID_PARAM = "SELECT * FROM %s WHERE %s = ?;";

    public String createSelectByIdQuery(Class<?> entityType) {
        String tableName = resolveTableName(entityType);
        String idColumnName = EntityUtils.resolveIdColumnName(entityType);
        return String.format(SELECT_FROM_TABLE_BY_ID_PARAM, tableName, idColumnName);
    }

    // Todo: tableName resolver
    private String resolveTableName(Class<?> entityType) {
        log.trace("Resolving table name for entity {}", entityType);
        if (entityType.isAnnotationPresent(Table.class)) {
            String explicitName = entityType.getDeclaredAnnotation(Table.class).value();
            if (StringUtils.isNotBlank(explicitName)) {
                log.trace("Table is specified explicitly as {}", explicitName);
                return explicitName;
            }
        }
        String tableName = entityType.getSimpleName().toLowerCase();
        log.trace("Table is explicitly specified, falling back to call name {}", tableName);
        return tableName;
    }
}
