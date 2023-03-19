package com.bobocode.svydovets.bibernate.validation.annotation.required;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_NO_ID;

import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.util.Arrays;

public class IdValidatorImpl extends AbstractRequiredValidator {

    @Override
    public void validateEntity(Class<?> type) {
        checkHasId(type);
    }

    @Override
    public boolean support(Operation operation) {
        return supportedOperations.contains(operation);
    }

    private void checkHasId(Class<?> type) {
        Arrays.stream(type.getDeclaredFields())
                .filter(EntityUtils::isIdField)
                .findAny()
                .orElseThrow(
                        () -> new EntityValidationException(String.format(CLASS_HAS_NO_ID, type.getName())));
    }
}
