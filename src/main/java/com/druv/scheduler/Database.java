package com.druv.scheduler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:smart_scheduler.db";

    // Get a connection to the SQLite DB
    public static Connection connect() throws java.sql.SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Read schema.sql from resources and create tables
    public static void initialize() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            // Load and verify schema.sql
            InputStream in = Database.class.getResourceAsStream("/schema.sql");
            if (in == null) {
                System.err.println("ERROR: schema.sql not found in:");
                System.err.println("- src/main/resources/schema.sql");
                System.err.println("- target/classes/schema.sql");
                throw new IllegalStateException("schema.sql not found in classpath");
            }

            // Read SQL content using BufferedReader
            String sql = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

            if (sql.trim().isEmpty()) {
                System.err.println("ERROR: schema.sql exists but is empty");
                throw new IllegalStateException("schema.sql is empty");
            }

            System.out.println("Found schema.sql with length: " + sql.length());
            System.out.println("Executing SQL schema:\n" + sql);
            
            // Execute each statement separately
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.executeUpdate(statement);
                }
            }
            System.out.println("Database initialized successfully.");
            
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

