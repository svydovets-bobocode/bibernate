package com.bobocode.svydovets.bibernate.validation.annotation.required;

import static com.bobocode.svydovets.bibernate.constant.ErrorMessage.CLASS_HAS_UNSUPPORTED_VERSION_TYPE;

import com.bobocode.svydovets.bibernate.annotation.Version;
import com.bobocode.svydovets.bibernate.constant.Operation;
import com.bobocode.svydovets.bibernate.exception.EntityValidationException;
import com.bobocode.svydovets.bibernate.util.EntityUtils;
import java.lang.reflect.Field;
import java.util.Optional;

public class VersionValidatorImpl extends AbstractRequiredValidator {

    @Override
    public void validateEntity(Class<?> type) {
        Optional<Field> optionalVersionField = EntityUtils.findVersionField(type);
        optionalVersionField.ifPresent(
                version -> checkVersionFieldIsValid(version.getType(), type.getName()));
    }

    @Override
    public boolean support(Operation operation) {
        return supportedOperations.contains(operation);
    }

    private void checkVersionFieldIsValid(Class<?> versionFieldType, String scannedClassName) {
        if (!Version.SUPPORTED_OBJECT_TYPES.contains(versionFieldType)) {
            throw new EntityValidationException(
                    String.format(
                            CLASS_HAS_UNSUPPORTED_VERSION_TYPE, scannedClassName, versionFieldType.getName()));
        }
    }
}
