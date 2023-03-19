package com.bobocode.svydovets.bibernate.util;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CAN_NOT_CREATE_A_SNAPSHOT_OF_ENTITY;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.testdata.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ObjectCloneUtilsTest {

    private final ObjectMapper objectMapper = mock(ObjectMapper.class);

    @Test
    void deepCopySuccessWhenInputIsValid() throws JsonProcessingException {
        User user = new User();
        String json = "{id: 1, name: \"Roman\"}";
        when(objectMapper.writeValueAsString(user)).thenReturn(json);
        when(objectMapper.readValue(json, Object.class)).thenReturn(user);

        User copyResult = ObjectCloneUtils.deepCopy(user, User.class);

        assertNotEquals(user, copyResult);
    }

    @Test
    void deepCopyThrowsBeanExceptionWhenInputIsInvalid() throws JsonProcessingException {
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());
        Object objectCopy = new Object();
        assertThrows(
                BibernateException.class,
                () -> ObjectCloneUtils.deepCopy(objectCopy, Object.class),
                CAN_NOT_CREATE_A_SNAPSHOT_OF_ENTITY);
    }
}
