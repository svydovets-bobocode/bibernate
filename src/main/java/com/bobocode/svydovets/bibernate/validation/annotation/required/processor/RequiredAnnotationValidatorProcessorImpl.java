package com.bobocode.svydovets.bibernate.validation.annotation.required.processor;

import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.validation.annotation.required.AbstractRequiredValidator;
import com.bobocode.svydovets.bibernate.validation.annotation.required.EntityValidatorImpl;
import com.bobocode.svydovets.bibernate.validation.annotation.required.IdValidatorImpl;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequiredAnnotationValidatorProcessorImpl
        implements RequiredAnnotationValidatorProcessor {

    private static final Set<AbstractRequiredValidator> validators = new LinkedHashSet<>();

    public RequiredAnnotationValidatorProcessorImpl() {
        initValidators();
    }

    private void initValidators() {
        validators.add(new EntityValidatorImpl());
        validators.add(new IdValidatorImpl());
    }

    @Override
    public void validate(Class<?> type, Operation operation) {
        log.info("Validation {}", type.getName());
        validators.stream()
                .filter(validator -> validator.support(operation))
                .forEach(validator -> validator.validate(type));
    }

    @Override
    public void validate(Class<?> type) {
        log.info("Validation {}", type.getName());
        validators.forEach(validator -> validator.validate(type));
    }
}
