package com.bobocode.svydovets.bibernate.session;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionFactoryImpl implements SessionFactory {
    private final DataSource dataSource;

    private static volatile SessionFactoryImpl instance;

    public static SessionFactoryImpl getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (SessionFactoryImpl.class) {
                if (instance == null) {
                    instance = new SessionFactoryImpl(dataSource);
                }
            }
        }
        return instance;
    }

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
