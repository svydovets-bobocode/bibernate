package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import lombok.Data;

/**
 * An abstract implementation of the {@link Action} interface that provides common functionality for
 * executing database operations on a specified object.
 *
 * @param <T> the type of object on which the database operation is to be performed
 */
@Data
public abstract class AbstractAction<T> implements Action {
    protected final Connection connection;
    protected final T actionObject;

    /** Executes the database operation by invoking the {@link #doExecute()} method. */
    @Override
    public void execute() {
        doExecute();
    }

    /** Performs the database operation. This method is to be implemented by subclasses. */
    protected abstract void doExecute();

    /**
     * Sets the fields of the prepared statement with the values from the corresponding fields of the
     * action object.
     *
     * @param preparedStatement the prepared statement to which the fields are to be set
     * @param fields the fields whose values are to be set in the prepared statement
     * @throws SQLException if an error occurs while setting the field values in the prepared
     *     statement
     */
    protected void setFieldsInPreparedStatement(PreparedStatement preparedStatement, Field[] fields)
            throws SQLException {
        Optional<Field> optVersionField = EntityUtils.findVersionField(actionObject.getClass());
        for (int i = 0; i < fields.length; i++) {
            Field declaredField = fields[i];
            Optional<Object> optFieldValue =
                    EntityUtils.retrieveValueFromField(actionObject, declaredField);

            boolean versionField =
                    optVersionField.isPresent()
                            && declaredField.getName().equals(optVersionField.get().getName());
            if (versionField) {
                optFieldValue = Optional.of(resolveVersionField(optFieldValue.orElse(0)));
            }

            if (optFieldValue.isPresent()) {
                preparedStatement.setObject(i + 1, optFieldValue.get());
            }
        }
    }

    /**
     * Resolves the version field value of the action object.
     *
     * @param versionFieldValue the current value of the version field
     * @return the resolved value of the version field
     */
    protected long resolveVersionField(Object versionFieldValue) {
        long value = ((Number) versionFieldValue).longValue();
        return value + 1;
    }
}
