package com.druv.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class Database {
    private static final String DB_NAME = System.getProperty("db.name", "smart_scheduler.db");
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_NAME;

    // Get a connection to the SQLite DB
    public static Connection connect() throws java.sql.SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    // Read schema.sql from resources and create tables
    public static void initialize() {
        try {
            // Load schema.sql from resources
            String schema;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        Database.class.getResourceAsStream("/schema.sql"), 
                        StandardCharsets.UTF_8))) {
                schema = reader.lines().collect(Collectors.joining("\n"));
            }
            
            System.out.println("Executing database schema...");
            try (Connection conn = connect();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(schema);
            }
            System.out.println("Database initialized successfully.");
            
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            System.exit(1);
        }
    }
}

