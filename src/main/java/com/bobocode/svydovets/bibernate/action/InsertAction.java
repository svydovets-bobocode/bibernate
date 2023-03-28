package com.bobocode.svydovets.bibernate.action;

import static com.bobocode.svydovets.bibernate.util.EntityUtils.getInsertableFields;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.retrieveIdField;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.retrieveValueFromField;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertAction extends AbstractAction {

    public InsertAction(Connection connection, Object actionObject) {
        super(connection, actionObject);
    }

    @Override
    protected void doExecute() {
        var actionObjectType = actionObject.getClass();
        try (var preparedStatement =
                connection.prepareStatement(
                        SqlQueryBuilder.createInsertQuery(actionObjectType), Statement.RETURN_GENERATED_KEYS)) {
            Field[] insertableFields = getInsertableFields(actionObjectType);

            Field idField = retrieveIdField(actionObjectType.getDeclaredFields());

            if (retrieveValueFromField(actionObject, idField).isPresent()) {
                throw new BibernateException("The Id value is autogenerated and shouldn't be provided");
            }

            for (int i = 0; i < insertableFields.length; i++) {
                Field declaredField = insertableFields[i];
                preparedStatement.setObject(
                        i + 1, retrieveValueFromField(actionObject, declaredField).orElseThrow());
            }
            preparedStatement.execute();

            preparedStatement.getGeneratedKeys().next();
            var id = preparedStatement.getGeneratedKeys().getObject(1, idField.getType());
            EntityUtils.setValueToField(actionObject, idField, id);
        } catch (SQLException ex) {
            throw new BibernateException(ex);
        }
    }

    @Override
    public ActionType getActionType() {
        return ActionType.INSERT;
    }
}
