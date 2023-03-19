package com.bobocode.svydovets.bibernate.validation.annotation.required.processor;

import com.bobocode.svydovets.bibernate.constant.Operation;

public interface RequiredAnnotationValidatorProcessor {

    void validate(Class<?> type, Operation operation);

    void validate(Class<?> type);
}
