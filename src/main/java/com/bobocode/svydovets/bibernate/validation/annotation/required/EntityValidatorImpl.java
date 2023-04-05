package com.bobocode.svydovets.bibernate.validation.annotation.required;

import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.util.EntityUtils;

public class EntityValidatorImpl extends AbstractRequiredValidator {

    @Override
    public void validateEntity(Class<?> type) {
        EntityUtils.checkIsEntity(type);
        EntityUtils.checkRelationsConfiguration(type);
    }

    @Override
    public boolean support(Operation operation) {
        return supportedOperations.contains(operation);
    }
}
