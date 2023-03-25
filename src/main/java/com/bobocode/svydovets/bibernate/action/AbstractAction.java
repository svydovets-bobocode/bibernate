package com.bobocode.svydovets.bibernate.action;

import java.sql.Connection;
import lombok.Data;

@Data
public abstract class AbstractAction implements Action {
    protected final Connection connection;
    protected final Object actionObject;

    @Override
    public void execute() {
        doExecute();
    }

    protected abstract void doExecute();
}
