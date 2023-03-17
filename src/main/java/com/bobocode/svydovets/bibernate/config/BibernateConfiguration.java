package com.bobocode.svydovets.bibernate.config;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.session.SessionFactory;
import com.bobocode.svydovets.bibernate.session.SessionFactoryImpl;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// todo: add JavaDoc and README guide.
// todo: This is the starting point for our consumers to use the framework
public class BibernateConfiguration {
    private ConfigurationSource source;
    private final DataSource dataSource;
    private final SqlQueryBuilder sqlQueryBuilder;

    // todo: should it be public?
    // todo: Consumer should use only "configure" and "buildSessionFactory" methods
    public BibernateConfiguration(DataSource dataSource, SqlQueryBuilder sqlQueryBuilder) {
        this.dataSource = dataSource;
        this.sqlQueryBuilder = sqlQueryBuilder;
    }

    // todo: add JavaDoc and README guide how to use default configuration
    public BibernateConfiguration configure() {
        return configure(new PropertyFileConfiguration("bibernate.properties"));
    }

    // todo: add JavaDoc and README guide how to configure with different configuration sources
    public BibernateConfiguration configure(ConfigurationSource source) {
        log.info("Configuring with source: {}", source);
        this.source = source;
        return this;
    }

    // todo: add JavaDoc and README guide how to obtain the SessionFactory.
    public SessionFactory buildSessionFactory() {
        if (source == null) {
            throw new IllegalStateException(
                    "Configuration source must be set before building the SessionFactory.");
        }
        log.info(
                "Building SessionFactory with DataSource: {} and SqlQueryBuilder: {}",
                dataSource,
                sqlQueryBuilder);
        // todo: Should we return new every time?
        // todo: Maybe it should be the single object of the SessionFactory per app
        return new SessionFactoryImpl(dataSource, sqlQueryBuilder);
    }
}
