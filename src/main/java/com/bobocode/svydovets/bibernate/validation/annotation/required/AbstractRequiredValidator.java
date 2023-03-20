package com.bobocode.svydovets.bibernate.validation.annotation.required;

import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import com.bobocode.svydovets.bibernate.validation.Validator;
import java.util.Set;

public abstract class AbstractRequiredValidator implements Validator {

    protected static final Set<Operation> supportedOperations = Set.of(Operation.values());

    @Override
    public void validate(Class<?> type) {
        EntityUtils.checkHasNoArgConstructor(type);
        validateEntity(type);
    }

    abstract void validateEntity(Class<?> type);
}
