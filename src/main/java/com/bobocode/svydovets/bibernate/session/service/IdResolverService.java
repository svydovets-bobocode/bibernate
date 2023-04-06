package com.bobocode.svydovets.bibernate.session.service;

import static com.bobocode.svydovets.bibernate.constant.GenerationType.MANUAL;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveIdColumnField;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.retrieveIdValue;

import com.bobocode.svydovets.bibernate.annotation.GeneratedValue;
import com.bobocode.svydovets.bibernate.constant.GenerationType;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import java.lang.reflect.Field;
import java.sql.Connection;

public class IdResolverService {

    private final IdValuePopulatorFactory idValuePopulatorFactory;

    public IdResolverService() {
        this.idValuePopulatorFactory = IdValuePopulatorFactory.getInstance();
    }

    public void resolveIdValue(Connection connection, Object entity) {
        Class<?> entityType = entity.getClass();
        Field idField = resolveIdColumnField(entityType);
        var generationValueType = getGenerationValueType(idField);
        var idValue = retrieveIdValue(entity);

        if (idValue.isPresent() && generationValueType.equals(MANUAL)) {
            return;
        } else if (idValue.isEmpty() && generationValueType.equals(MANUAL)) {
            throw new EntityValidationException(
                    "Id value is missing for entity: " + entityType.getSimpleName());
        }

        if (idValue.isEmpty()) {
            var idValuePopulator = idValuePopulatorFactory.getIdValuePopulator(generationValueType);
            idValuePopulator.populateIdValue(connection, entity);
        }
    }

    private GenerationType getGenerationValueType(Field idField) {
        if (idField.isAnnotationPresent(GeneratedValue.class)) {
            var annotation = idField.getAnnotation(GeneratedValue.class);
            return annotation.strategy();
        } else {
            return MANUAL;
        }
    }
}
