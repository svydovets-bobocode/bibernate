package com.bobocode.svydovets.bibernate.constant;

public final class ErrorMessage {
    private ErrorMessage() {}

    public static final String CLASS_HAS_NO_ENTITY_ANNOTATION =
            "Class '%s' has no @Entity annotation (every entity class must be annotated with '@Entity')";
    public static final String CLASS_HAS_NO_ARG_CONSTRUCTOR =
            "Entity '%s' has no 'no-arg constructor' (every '@Entity' class must declare 'no-arg constructor')";
    public static final String CAN_NOT_CREATE_A_SNAPSHOT_OF_ENTITY =
            "Can not create a snapshot object for entity";
    public static final String CLASS_HAS_NO_ID =
            "Entity '%s' does not have a field annotated with @Id "
                    + "(every '@Entity' class must have a field annotated with @Id')";
    public static final String CLASS_HAS_MORE_THAN_ONE_ID =
            "Entity '%s' has %d fields annotated with @Id "
                    + "(every '@Entity' class must have only one field annotated with @Id')";
    public static final String CLASS_HAS_UNSUPPORTED_ID_TYPE =
            "Entity '%s' has unsupported @Id type %s "
                    + "('@Id' field in '@Entity' must have one of the supported types. "
                    + "See supported types in '@Id' documentation')";

    public static final String ERROR_MAPPING_RESULT_SET_TO_OBJECT =
            "Error while mapping result set to object of type '%s'";
    public static final String ERROR_RETRIEVING_VALUE_FROM_FIELD =
            "Error while retrieving the value from field '%s' in type '%s'";
    public static final String ERROR_GETTING_FIELD_VALUES_FROM_ENTITY =
            "Error while retrieving values from entity '%s'";
    public static final String ERROR_SETTING_VALUE_TO_FIELD =
            "Error while setting the value `%s` to field '%s' in type '%s'";

    public static final String TRANSACTION_NOT_STARTED_OR_ALREADY_COMMITTED =
            "Transaction not started or already committed";
    public static final String TRANSACTION_IS_ALREADY_STARTED = "Transaction is already started";
    public static final String ERROR_WHILE_BEGINNING_TRANSACTION =
            "An error occurred while beginning transaction";
    public static final String ERROR_WHILE_COMMITTING_TRANSACTION =
            "An error occurred while committing transaction";
    public static final String ERROR_WHILE_ROLLING_BACK_TRANSACTION =
            "An error occurred while rolling back transaction";

    public static final String SESSION_IS_CLOSED =
            "Session is closed (session must be in the opened state to perform any operation in the database)";

    public static final String CLASS_HAS_MORE_THAN_ONE_VERSION =
            "Entity '%s' has %d fields annotated with @Version "
                    + "(every '@Entity' class must have zero or only one field annotated with @Version')";

    public static final String CLASS_HAS_UNSUPPORTED_VERSION_TYPE =
            "Entity '%s' has unsupported @Version type %s "
                    + "('@Version' field in '@Entity' must have one of the supported types. "
                    + "See supported types in '@Version' documentation')";
}
