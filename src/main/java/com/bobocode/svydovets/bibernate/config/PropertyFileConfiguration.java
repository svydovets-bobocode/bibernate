package com.bobocode.svydovets.bibernate.config;

import com.bobocode.svydovets.bibernate.exception.ConfigurationException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class PropertyFileConfiguration implements ConfigurationSource {
    public static final String FAILED_TO_LOAD_CONFIGURATION_PROPERTIES_FROM_FILE =
            "Failed to load configuration";
    public static final String CREATE_A_CONFIG_FILE_OR_USE_JAVA_CONFIGURATION =
            "Create a config file in `src/main/resources` directory or use Java configuration.";
    private final Properties properties;

    public PropertyFileConfiguration(String fileName) {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            Optional.ofNullable(inputStream)
                    .orElseThrow(
                            () -> new ConfigurationException(CREATE_A_CONFIG_FILE_OR_USE_JAVA_CONFIGURATION));
            properties.load(inputStream);
        } catch (Exception e) {
            throw new ConfigurationException(FAILED_TO_LOAD_CONFIGURATION_PROPERTIES_FROM_FILE, e);
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
