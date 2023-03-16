package com.bobocode.svydovets.bibernate.config;

import java.util.HashMap;
import java.util.Map;

public class HashMapConfiguration implements ConfigurationSource {
    private final Map<String, String> properties;

    public HashMapConfiguration(Map<String, String> properties) {
        this.properties = properties == null ? new HashMap<>() : new HashMap<>(properties);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }
}
