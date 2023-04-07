package com.bobocode.svydovets.bibernate.action;

/**
 * An interface representing an action to be executed. Actions can be compared based on their
 * priority level.
 */
public interface Action extends Comparable<Action> {

    /** Executes the action. */
    void execute();

    /**
     * Gets the type of the action.
     *
     * @return the type of the action.
     */
    ActionType getActionType();

    /**
     * Compares the priority of this action with the priority of another action.
     *
     * @param other the other action to compare with.
     * @return a negative integer, zero, or a positive integer as this action has lower priority,
     *     equal priority, or higher priority than the other action.
     */
    @Override
    default int compareTo(Action other) {
        return Integer.compare(getActionType().getPriority(), other.getActionType().getPriority());
    }
}
