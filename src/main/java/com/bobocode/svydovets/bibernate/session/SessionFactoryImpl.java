package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.sql.Connection;
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
            Connection connection = dataSource.getConnection();
            SearchService searchService = new SearchService(connection);
            return new SessionImpl(connection, searchService);
        } catch (SQLException e) {
            throw new BibernateException("An error occurred while opening session", e);
        }
    }
}
