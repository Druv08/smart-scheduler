package com.druv.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DatabaseTest {
    
    @BeforeAll
    static void setup() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            fail("SQLite JDBC driver not found");
        }
    }

    @Test
    public void testDatabaseInitialization() {
        try {
            // Initialize the database (creates tables if not present)
            Database.initialize();

            // Try connecting and checking if the 'users' table exists
            try (Connection conn = Database.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name='users';"
                 )) {

                assertTrue(rs.next(), "Table 'users' should exist after initialization.");
                System.out.println("Database initialized successfully, and 'users' table found.");
            }

        } catch (Exception e) {
            fail("Database initialization test failed: " + e.getMessage());
        }
    }
}
mvn clean test