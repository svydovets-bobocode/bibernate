package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionFactoryImpl implements SessionFactory {
    private final DataSource dataSource;
    private final SqlQueryBuilder sqlQueryBuilder;

    @Override
    public Session openSession() {
        try {
            SelectAction selectAction = new SelectAction(dataSource, sqlQueryBuilder);
            return new SessionImpl(selectAction, dataSource.getConnection());
        } catch (SQLException e) {
            throw new BibernateException("An error occurred while opening session", e);
        }
    }
}
