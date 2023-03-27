package com.bobocode.svydovets.bibernate.testdata.factory;

import java.util.HashMap;

public class PropertiesFactory {

    public static final String POSTGRES_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    public static final String POSTGRES_DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String POSTGRES_DB_USERNAME = "postgres";
    public static final String POSTGRES_DB_PASSWORD = "postgres";

    public static final String H2_DRIVER_CLASS_NAME = "org.h2.Driver";
    public static final String H2_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    public static final String H2_DB_USERNAME = "sa";
    public static final String H2_DB_PASSWORD = "";

    public static final String POSTGRES_TEST_DB_URL = "jdbc:postgresql://localhost:5432/testdatabase";
    public static final String POSTGRES_TEST_DB_USERNAME = "testuser";
    public static final String POSTGRES_TEST_DB_PASSWORD = "testpassword";

    public static HashMap<String, String> getValidPostgresProperties() {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("svydovets.bibernate.driverClassName", POSTGRES_DRIVER_CLASS_NAME);
        properties.put("svydovets.bibernate.db.url", POSTGRES_DB_URL);
        properties.put("svydovets.bibernate.db.username", POSTGRES_DB_USERNAME);
        properties.put("svydovets.bibernate.db.password", POSTGRES_DB_PASSWORD);
        return properties;
    }

    public static HashMap<String, String> getValidH2Properties() {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("svydovets.bibernate.driverClassName", H2_DRIVER_CLASS_NAME);
        properties.put("svydovets.bibernate.db.url", H2_DB_URL);
        properties.put("svydovets.bibernate.db.username", H2_DB_USERNAME);
        properties.put("svydovets.bibernate.db.password", H2_DB_PASSWORD);
        return properties;
    }
}
