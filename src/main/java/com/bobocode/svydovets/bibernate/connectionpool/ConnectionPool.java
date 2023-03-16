package com.bobocode.svydovets.bibernate.connectionpool;

import com.bobocode.svydovets.bibernate.config.ConfigurationSource;
import javax.sql.DataSource;

public interface ConnectionPool {
    DataSource getDataSource(ConfigurationSource source);
}
