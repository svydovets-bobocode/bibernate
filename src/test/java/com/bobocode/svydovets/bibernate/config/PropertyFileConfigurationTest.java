package com.bobocode.svydovets.bibernate.config;

import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.H2_DB_PASSWORD;
import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.H2_DB_URL;
import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.H2_DB_USERNAME;
import static com.bobocode.svydovets.bibernate.testdata.factory.PropertiesFactory.H2_DRIVER_CLASS_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyFileConfigurationTest {

    private ConfigurationSource source;

    @BeforeEach
    public void setUp() {
        source = new PropertyFileConfiguration("test_svydovets_bibernate_h2.properties");
    }

    @Test
    public void shouldGetProperties() {
        assertEquals(H2_DRIVER_CLASS_NAME, source.getProperty("svydovets.bibernate.driverClassName"));
        assertEquals(H2_DB_URL, source.getProperty("svydovets.bibernate.db.url"));
        assertEquals(H2_DB_USERNAME, source.getProperty("svydovets.bibernate.db.username"));
        assertEquals(H2_DB_PASSWORD, source.getProperty("svydovets.bibernate.db.password"));
    }

    @Test
    public void testGetNonExistingProperty() {
        assertNull(source.getProperty("non.existing.property"));
    }
}
