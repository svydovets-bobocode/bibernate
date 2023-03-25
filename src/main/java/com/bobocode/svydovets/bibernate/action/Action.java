package com.bobocode.svydovets.bibernate.action;

public interface Action {

    void execute();

    ActionType getActionType();
}
