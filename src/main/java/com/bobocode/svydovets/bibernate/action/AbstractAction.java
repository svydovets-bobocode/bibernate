package com.bobocode.svydovets.bibernate.action;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import lombok.Data;

import javax.sql.DataSource;
import java.sql.Connection;

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
