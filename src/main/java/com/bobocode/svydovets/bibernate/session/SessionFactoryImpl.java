package com.bobocode.svydovets.bibernate.session;

import static com.bobocode.svydovets.bibernate.util.LogoUtils.getBibernateLogo;

import com.bobocode.svydovets.bibernate.exception.BibernateException;
import com.bobocode.svydovets.bibernate.session.service.SearchService;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SessionFactoryImpl implements SessionFactory {
    private final DataSource dataSource;

    private static volatile SessionFactoryImpl instance;

    public static SessionFactoryImpl getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (SessionFactoryImpl.class) {
                if (instance == null) {
                    log.info(getBibernateLogo());
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
