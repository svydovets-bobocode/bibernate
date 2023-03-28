package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.SelectAction;
import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionFactoryImpl implements SessionFactory {
    private final DataSource dataSource;
    private final SqlQueryBuilder sqlQueryBuilder;

    private static volatile SessionFactoryImpl instance;

    public static SessionFactoryImpl getInstance(
            DataSource dataSource, SqlQueryBuilder sqlQueryBuilder) {
        if (instance == null) {
            synchronized (SessionFactoryImpl.class) {
                if (instance == null) {
                    instance = new SessionFactoryImpl(dataSource, sqlQueryBuilder);
                }
            }
        }
        return instance;
    }

    @Override
    public Session openSession() {
        try {
            Connection connection = dataSource.getConnection();
            SelectAction selectAction = new SelectAction(connection, sqlQueryBuilder);
            return new SessionImpl(selectAction, connection);
        } catch (SQLException e) {
            throw new BibernateException("An error occurred while opening session", e);
        }
    }
}
