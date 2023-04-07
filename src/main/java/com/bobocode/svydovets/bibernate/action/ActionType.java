package com.bobocode.svydovets.bibernate.action;

/** An enumeration representing the type of an action that can be performed. */
public enum ActionType {
    INSERT(1),
    UPDATE(2),
    REMOVE(3);

    private final int priority;

    ActionType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
