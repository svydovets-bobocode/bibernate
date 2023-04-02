package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.key.EntityKey;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionQueue {

    public Map<EntityKey<?>, List<Action>> getActionsMap() {
        return actionsMap;
    }

    private final Map<EntityKey<?>, List<Action>> actionsMap = new ConcurrentHashMap<>();

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

    public void executeAll() {
        for (Map.Entry<EntityKey<?>, List<Action>> entry : actionsMap.entrySet()) {

            var actions = filterDuplicateSaveActions(entry.getValue());

            // todo: additional omptimizations reorder quries etc.

            // Execute optimized actions
            for (Action action : actions) {
                action.execute();
                log.debug("Executing action: {}", action.getActionType());
            }
        }
    }

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

    public void clear() {
        actionsMap.clear();
    }
}
