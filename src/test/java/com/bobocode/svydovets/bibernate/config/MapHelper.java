package com.bobocode.svydovets.bibernate.config;

import java.util.HashMap;
import java.util.Map;

public class MapHelper {

    public static Map<String, String> properties = new HashMap<>();

    static {
        properties.put("svydovets.bibernate.driverClassName", "org.postgresql.Driver");
        properties.put("svydovets.bibernate.db.url", "jdbc:postgresql://localhost:5432/postgres");
        properties.put("svydovets.bibernate.db.username", "postgres");
        properties.put("svydovets.bibernate.db.password", "postgres");
    }
}
