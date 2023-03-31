package com.bobocode.svydovets.bibernate.validation.annotation.required;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_MORE_THAN_ONE_VERSION;
import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_UNSUPPORTED_VERSION_TYPE;

import com.bobocode.svydovets.bibernate.annotation.Version;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VersionValidatorImpl extends AbstractRequiredValidator {

    @Override
    public void validateEntity(Class<?> type) {
        Optional<Field> optionalVersionField = findVersionField(type);
        optionalVersionField.ifPresent(
                version -> checkVersionFieldIsValid(version.getType(), type.getName()));
    }

    @Override
    public boolean support(Operation operation) {
        return supportedOperations.contains(operation);
    }

    private Optional<Field> findVersionField(Class<?> type) {
        List<Field> versionFields =
                Arrays.stream(type.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Version.class))
                        .toList();

        if (versionFields.size() > 1) {
            throw new EntityValidationException(
                    String.format(CLASS_HAS_MORE_THAN_ONE_VERSION, type.getName(), versionFields.size()));
        }
        return versionFields.stream().findFirst();
    }

    private void checkVersionFieldIsValid(Class<?> versionFieldType, String scannedClassName) {
        if (!Version.SUPPORTED_OBJECT_TYPES.contains(versionFieldType)) {
            throw new EntityValidationException(
                    String.format(
                            CLASS_HAS_UNSUPPORTED_VERSION_TYPE, scannedClassName, versionFieldType.getName()));
        }
    }
}
