package com.bobocode.svydovets.bibernate.validation.annotation.required;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_MORE_THAN_ONE_ID;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ID;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_UNSUPPORTED_ID_TYPE;

import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class IdValidatorImpl extends AbstractRequiredValidator {

    @Override
    public void validateEntity(Class<?> type) {
        checkHasValidId(type);
    }

    @Override
    public boolean support(Operation operation) {
        return supportedOperations.contains(operation);
    }

    private void checkHasValidId(Class<?> type) {
        List<Field> idFields =
                Arrays.stream(type.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Id.class))
                        .toList();

        if (idFields.size() == 0) {
            throw new EntityValidationException(String.format(CLASS_HAS_NO_ID, type.getName()));
        }
        if (idFields.size() > 1) {
            throw new EntityValidationException(
                    String.format(CLASS_HAS_MORE_THAN_ONE_ID, type.getName(), idFields.size()));
        }

        Class<?> idType = idFields.get(0).getType();
        if (!Id.SUPPORTED_OBJECT_TYPES.contains(idType)) {
            throw new EntityValidationException(
                    String.format(CLASS_HAS_UNSUPPORTED_ID_TYPE, type.getName(), idType.getName()));
        }
    }
}
