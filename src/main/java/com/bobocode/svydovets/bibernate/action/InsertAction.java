package com.bobocode.svydovets.bibernate.action;

import static com.bobocode.svydovets.bibernate.util.EntityUtils.getInsertableFields;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertAction<T> extends AbstractAction<T> {

    public InsertAction(Connection connection, T actionObject) {
        super(connection, actionObject);
    }

    @Override
    protected void doExecute() {
        var actionObjectType = actionObject.getClass();
        try (var preparedStatement =
                connection.prepareStatement(
                        SqlQueryBuilder.createInsertQuery(actionObjectType), Statement.RETURN_GENERATED_KEYS)) {
            Field[] insertableFields = getInsertableFields(actionObjectType);
            Field field =
                    Arrays.stream(actionObjectType.getDeclaredFields())
                            .filter(EntityUtils::isIdField)
                            .findFirst()
                            .orElseThrow();

            // TODO: validate if ID already present

            setFieldsInPreparedStatement(preparedStatement, insertableFields);
            preparedStatement.execute();

            preparedStatement.getGeneratedKeys().next();
            var id = preparedStatement.getGeneratedKeys().getObject(1, field.getType());
            EntityUtils.setValueToField(actionObject, field, id);
        } catch (SQLException ex) {
            throw new BibernateException(ex);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.INSERT;
    }
}