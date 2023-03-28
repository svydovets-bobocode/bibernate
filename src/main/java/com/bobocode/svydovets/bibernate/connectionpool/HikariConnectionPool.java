package com.bobocode.svydovets.bibernate.connectionpool;

import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class HikariConnectionPool implements ConnectionPool {

    @Override
    public DataSource getDataSource(ConfigurationSource source) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(source.getProperty("svydovets.bibernate.driverClassName"));
        config.setJdbcUrl(source.getProperty("svydovets.bibernate.db.url"));
        config.setUsername(source.getProperty("svydovets.bibernate.db.username"));
        config.setPassword(source.getProperty("svydovets.bibernate.db.password"));

        return new HikariDataSource(config);
    }
}
