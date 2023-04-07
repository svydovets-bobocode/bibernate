package com.bobocode.svydovets.bibernate.session.service;

import static com.bobocode.svydovets.bibernate.constant.GenerationType.MANUAL;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.resolveIdColumnField;
import static com.bobocode.svydovets.bibernate.util.EntityUtils.retrieveIdValue;

import com.bobocode.svydovets.bibernate.annotation.GeneratedValue;
import com.bobocode.svydovets.bibernate.constant.GenerationType;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import java.lang.reflect.Field;
import java.sql.Connection;

/**
 * IdResolverService is responsible for resolving the ID value of an entity based on the GenerationType strategy.
 */
public class IdResolverService {

    private final IdValuePopulatorFactory idValuePopulatorFactory;

    public IdResolverService() {
        this.idValuePopulatorFactory = new IdValuePopulatorFactory();
    }

    /**
     * Resolves the ID value of an entity based on the GenerationType strategy.
     * If the ID value is present and the strategy is MANUAL, no action is taken, skip the processing.
     * If the ID value is missing and the strategy is MANUAL, an EntityValidationException is thrown.
     * If the ID value is missing, the appropriate IdValuePopulator is determined from the IdValuePopulatorFactory
     * based on the GenerationType and the ID value is populated using that IdValuePopulator.
     *
     * @param connection the database connection
     * @param entity the entity whose ID value needs to be resolved
     */
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
