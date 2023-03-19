package com.bobocode.svydovets.bibernate.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class XmlConfigurationTest {

    private ConfigurationSource source;

    @BeforeEach
    public void setUp() {
        source = new XmlConfiguration("test_svydovets_bibernate_properties.xml");
    }

    @Test
    public void shouldGetProperties() {
        assertEquals(
                "org.postgresql.Driver", source.getProperty("svydovets.bibernate.driverClassName"));
        assertEquals(
                "jdbc:postgresql://localhost:5432/testdatabase",
                source.getProperty("svydovets.bibernate.db.url"));
        assertEquals("testuser", source.getProperty("svydovets.bibernate.db.username"));
        assertEquals("testpassword", source.getProperty("svydovets.bibernate.db.password"));
    }

    @Test
    public void shouldReturnNullForNonExistingProperty() {
        assertNull(source.getProperty("non.existing.property"));
    }

    @Test
    public void shouldVerifyEmptyPropertyValue() {
        assertEquals("", source.getProperty("empty.property"));
    }
}
