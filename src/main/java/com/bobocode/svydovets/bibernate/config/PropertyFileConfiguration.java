package com.bobocode.svydovets.bibernate.config;

import com.bobocode.svydovets.bibernate.exception.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileConfiguration implements ConfigurationSource {
    private final Properties properties;

    public PropertyFileConfiguration(String fileName) {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ConfigurationException("Failed to load configuration properties from file", e);
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
