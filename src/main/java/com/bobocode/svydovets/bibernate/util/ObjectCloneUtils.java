package com.bobocode.svydovets.bibernate.util;

import static com.bobocode.svydovets.bibernate.util.Constants.CAN_NOT_CREATE_A_SNAPSHOT_OF_ENTITY;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectCloneUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ObjectCloneUtils() {}

    public static <T> T deepCopy(T inputObject, Class<? extends T> beanType) {
        objectMapper.setVisibility(
                objectMapper
                        .getSerializationConfig()
                        .getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        String objectJson;
        try {
            objectJson = objectMapper.writeValueAsString(inputObject);
            return objectMapper.readValue(objectJson, beanType);
        } catch (JsonProcessingException e) {
            throw new BibernateException(CAN_NOT_CREATE_A_SNAPSHOT_OF_ENTITY);
        }
    }
}
