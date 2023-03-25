package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.executor.JdbcExecutor;
import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class InsertAction extends AbstractAction{

    public InsertAction(Connection connection, Object actionObject) {
        super(connection, actionObject);
    }

    @Override
    protected void doExecute() {
        try {
            var actionType = actionObject.getClass();
            var preparedStatement = connection.prepareStatement(SqlQueryBuilder.createInsertQuery(actionType), Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < actionType.getDeclaredFields().length; i++) {
                Field declaredField = actionType.getDeclaredFields()[i];
                declaredField.setAccessible(true);
                if (EntityUtils.isIdField(declaredField)) {
//                    preparedStatement.setObject(i + 1, null);
                    continue;
                }
                preparedStatement.setObject(i + 1, declaredField.get(actionObject));
            }
            preparedStatement.execute();

            preparedStatement.getGeneratedKeys().next();
            Long id = preparedStatement.getGeneratedKeys().getObject(1, Long.class);

            for (int i = 0; i < actionType.getDeclaredFields().length; i++) {
                Field declaredField = actionType.getDeclaredFields()[i];
                declaredField.setAccessible(true);
                if (EntityUtils.isIdField(declaredField)) {
                    preparedStatement.setObject(i + 1, id);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.INSERT;
    }
}
