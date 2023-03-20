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
            "Entity %s has no identifier (every '@Entity' class must declare or inherit at least one '@Id'";
}
