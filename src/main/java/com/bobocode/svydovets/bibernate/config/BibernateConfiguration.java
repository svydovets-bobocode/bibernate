package com.bobocode.svydovets.bibernate.config;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.session.SessionFactoryImpl;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BibernateConfiguration {
    private ConfigurationSource source;
    private final DataSource dataSource;
    private final SqlQueryBuilder sqlQueryBuilder;

    public BibernateConfiguration(DataSource dataSource, SqlQueryBuilder sqlQueryBuilder) {
        this.dataSource = dataSource;
        this.sqlQueryBuilder = sqlQueryBuilder;
    }

    public BibernateConfiguration configure() {
        return configure(new PropertyFileConfiguration("bibernate.properties"));
    }

    public BibernateConfiguration configure(ConfigurationSource source) {
        log.info("Configuring with source: {}", source);
        this.source = source;
        return this;
    }

    public SessionFactoryImpl buildSessionFactory() {
        if (source == null) {
            throw new IllegalStateException(
                    "Configuration source must be set before building the SessionFactory.");
        }
        log.info(
                "Building SessionFactory with DataSource: {} and SqlQueryBuilder: {}",
                dataSource,
                sqlQueryBuilder);
        return new SessionFactoryImpl(dataSource, sqlQueryBuilder);
    }
}
