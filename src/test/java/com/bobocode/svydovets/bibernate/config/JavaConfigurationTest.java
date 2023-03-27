package com.bobocode.svydovets.bibernate.config;

import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.POSTGRES_DB_PASSWORD;
import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.POSTGRES_DB_URL;
import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.POSTGRES_DB_USERNAME;
import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.POSTGRES_DRIVER_CLASS_NAME;
import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.getValidPostgresProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavaConfigurationTest {

    private ConfigurationSource source;

    @BeforeEach
    public void setUp() {
        source = new JavaConfiguration(getValidPostgresProperties());
    }

    @Test
    public void shouldGetProperties() {
        assertEquals(
                POSTGRES_DRIVER_CLASS_NAME, source.getProperty("svydovets.bibernate.driverClassName"));
        assertEquals(POSTGRES_DB_URL, source.getProperty("svydovets.bibernate.db.url"));
        assertEquals(POSTGRES_DB_USERNAME, source.getProperty("svydovets.bibernate.db.username"));
        assertEquals(POSTGRES_DB_PASSWORD, source.getProperty("svydovets.bibernate.db.password"));
    }

    @Test
    public void shouldReturnNullForNonExistingProperty() {
        assertNull(source.getProperty("non.existing.property"));
    }
}
