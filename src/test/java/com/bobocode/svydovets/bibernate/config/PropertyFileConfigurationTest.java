package com.bobocode.svydovets.bibernate.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertyFileConfigurationTest {

    private ConfigurationSource source;

    @BeforeEach
    public void setUp() {
        source = new PropertyFileConfiguration("test_svydovets_bibernate.properties");
    }

    @Test
    public void shouldReturnNullForNonExistingProperty() {
        assertEquals("org.h2.Driver", source.getProperty("svydovets.bibernate.driverClassName"));
        assertEquals(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", source.getProperty("svydovets.bibernate.db.url"));
        assertEquals("sa", source.getProperty("svydovets.bibernate.db.username"));
        assertEquals("", source.getProperty("svydovets.bibernate.db.password"));
    }

    @Test
    public void testGetNonExistingProperty() {
        assertNull(source.getProperty("non.existing.property"));
    }
}
