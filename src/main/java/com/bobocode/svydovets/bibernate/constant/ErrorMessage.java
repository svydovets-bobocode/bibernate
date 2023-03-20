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

    public static final String TRANSACTION_NOT_STARTED_OR_ALREADY_COMMITTED =
            "Transaction not started or already committed";
    public static final String TRANSACTION_IS_ALREADY_STARTED = "Transaction is already started";
    public static final String ERROR_WHILE_BEGINNING_TRANSACTION =
            "An error occurred while beginning transaction";
    public static final String ERROR_WHILE_COMMITTING_TRANSACTION =
            "An error occurred while committing transaction";
    public static final String ERROR_WHILE_ROLLING_BACK_TRANSACTION =
            "An error occurred while rolling back transaction";
}
