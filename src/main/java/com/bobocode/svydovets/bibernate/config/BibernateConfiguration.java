package com.bobocode.svydovets.bibernate.config;

import com.bobocode.svydovets.bibernate.action.query.SqlQueryBuilder;
import com.bobocode.svydovets.bibernate.connectionpool.HikariConnectionPool;
import com.bobocode.svydovets.bibernate.session.SessionFactory;
import com.bobocode.svydovets.bibernate.session.SessionFactoryImpl;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;


/**
 * BibernateConfiguration is the main configuration class for the Bibernate framework.
 * It provides methods for configuring the framework with different configuration sources,
 * and building a SessionFactory.
 */
@Slf4j
public class BibernateConfiguration {
    private ConfigurationSource source;
    private DataSource dataSource = null;
    private final SqlQueryBuilder sqlQueryBuilder = new SqlQueryBuilder();

    /**
     * Configures Bibernate with default configuration settings.
     * Default configuration is read from the "bibernate.properties" file.
     *
     * @return the configured BibernateConfiguration instance
     */
    public BibernateConfiguration configure() {
        return configure(new PropertyFileConfiguration("bibernate.properties"));
    }

    /**
     * Configures Bibernate with a custom configuration source.
     *
     * @param source the ConfigurationSource instance for custom configuration
     * @return the configured BibernateConfiguration instance
     */
    public BibernateConfiguration configure(ConfigurationSource source) {
        log.info("Configuring with source: {}", source);
        this.source = source;
        this.dataSource = new HikariConnectionPool().getDataSource(source);
        return this;
    }

    /**
     * Builds and returns a SessionFactory instance.
     * The method throws an IllegalStateException if the configuration source is not set.
     *
     * @return the SessionFactory instance
     * @throws IllegalStateException if the configuration source is not set
     */
    public SessionFactory buildSessionFactory() {
        if (source == null) {
            throw new IllegalStateException(
                    "Configuration source must be set before building the SessionFactory.");
        }
        log.info(
                "Building SessionFactory with DataSource: {} and SqlQueryBuilder: {}",
                dataSource,
                sqlQueryBuilder);
        return SessionFactoryImpl.getInstance(dataSource, sqlQueryBuilder);
    }
}
