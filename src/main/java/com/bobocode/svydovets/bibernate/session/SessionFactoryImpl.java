package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class SessionFactoryImpl implements SessionFactory {
    private final DataSource dataSource;
    private final SqlQueryBuilder sqlQueryBuilder;

    @Override
    public Session openSession() {
        SelectAction selectAction = new SelectAction(dataSource, sqlQueryBuilder);
        return new SessionImpl(selectAction);
    }
}
