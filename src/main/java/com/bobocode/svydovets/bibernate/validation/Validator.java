package com.bobocode.svydovets.bibernate.validation;

import com.bobocode.svydovets.bibernate.constant.Operation;

public interface Validator {

    void validate(Class<?> type);

    boolean support(Operation operation);
}
