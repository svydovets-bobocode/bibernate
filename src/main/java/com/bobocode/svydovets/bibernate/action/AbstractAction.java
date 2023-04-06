package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import lombok.Data;

@Data
public abstract class AbstractAction<T> implements Action {
    protected final Connection connection;
    protected final T actionObject;

    @Override
    public void execute() {
        doExecute();
    }

    protected abstract void doExecute();

    protected void setFieldsInPreparedStatement(PreparedStatement preparedStatement, Field[] fields)
            throws SQLException {
        Optional<Field> optVersionField = EntityUtils.findVersionField(actionObject.getClass());
        for (int i = 0; i < fields.length; i++) {
            Field declaredField = fields[i];
            Optional<Object> optFieldValue =
                    EntityUtils.retrieveValueFromField(actionObject, declaredField);
            if (optFieldValue.isPresent()) {
                boolean versionField =
                        optVersionField.isPresent()
                                && declaredField.getName().equals(optVersionField.get().getName());
                Object value =
                        versionField ? resolveVersionField(optFieldValue.get()) : optFieldValue.get();
                preparedStatement.setObject(i + 1, value);
            }
        }
    }

    protected long resolveVersionField(Object versionFieldValue) {
        long value = ((Number) versionFieldValue).longValue();
        return value + 1;
    }
}
