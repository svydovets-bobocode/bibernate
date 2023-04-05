package com.bobocode.svydovets.bibernate.action;

public interface Action extends Comparable<Action> {

    void execute();

    ActionType getActionType();

    @Override
    default int compareTo(Action other) {
        return Integer.compare(getActionType().getPriority(), other.getActionType().getPriority());
    }
}
