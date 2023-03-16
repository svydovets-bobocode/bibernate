package com.bobocode.svydovets.bibernate.config;

import static com.bobocode.svydovets.bibernate.config.MapHelper.properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavaConfigurationTest {

    private ConfigurationSource source;

    @BeforeEach
    public void setUp() {
        source = new JavaConfiguration(properties);
    }

    @Test
    public void shouldGetProperties() {
        assertEquals(
                "org.postgresql.Driver", source.getProperty("svydovets.bibernate.driverClassName"));
        assertEquals(
                "jdbc:postgresql://localhost:5432/postgres",
                source.getProperty("svydovets.bibernate.db.url"));
        assertEquals("postgres", source.getProperty("svydovets.bibernate.db.username"));
        assertEquals("postgres", source.getProperty("svydovets.bibernate.db.password"));
    }

    @Test
    public void shouldReturnNullForNonExistingProperty() {
        assertNull(source.getProperty("non.existing.property"));
    }
}
