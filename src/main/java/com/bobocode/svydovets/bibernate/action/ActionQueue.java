package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * ActionQueue is a class responsible for managing, optimizing, and executing actions in the correct
 * order. Actions are stored in a map that is keyed by EntityKey.
 */
@Slf4j
public class ActionQueue {
    public Map<EntityKey<?>, List<Action>> getActionsMap() {
        return actionsMap;
    }

    private final Map<EntityKey<?>, List<Action>> actionsMap = new ConcurrentHashMap<>();

    /**
     * Adds an action to the queue.
     *
     * @param entityKey the key for the entity associated with the action
     * @param action the action to add to the queue
     */
    public void addAction(EntityKey<?> entityKey, Action action) {
        actionsMap.compute(
                entityKey,
                (k, actionList) -> {
                    if (actionList == null) {
                        actionList = new ArrayList<>();
                    }
                    actionList.add(action);
                    return actionList;
                });
    }

    /** Executes all actions in the queue in the correct order (insert, update, remove). */
    public void executeAllWithOrder() {
        for (Map.Entry<EntityKey<?>, List<Action>> entry : actionsMap.entrySet()) {

            var actions = filterDuplicateSaveActions(entry.getValue());

            // Sort actions by actionType, so they will be executed in the order: insert, update, remove
            actions.sort(Comparator.comparing(Action::getActionType));

            // Execute optimized actions
            for (Action action : actions) {
                action.execute();
                log.debug("Executing action: {}", action.getActionType());
            }
        }
    }

    /**
     * Filters duplicate save actions from the given list of actions.
     *
     * @param actions the list of actions to filter
     * @return a list of actions with duplicates removed
     */
    private List<Action> filterDuplicateSaveActions(List<Action> actions) {
        LinkedHashSet<Action> uniqueInsertActions = new LinkedHashSet<>();
        List<Action> otherActions = new ArrayList<>();

        for (Action action : actions) {
            if (action.getActionType() == ActionType.INSERT) {
                uniqueInsertActions.add(action);
            } else {
                otherActions.add(action);
            }
        }

        List<Action> filteredActions = new ArrayList<>(uniqueInsertActions);
        filteredActions.addAll(otherActions);
        return filteredActions;
    }

    /** Clears all actions from the queue. */
    public void clear() {
        actionsMap.clear();
    }
}
