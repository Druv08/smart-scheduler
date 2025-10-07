package com.druv.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    
    private static final String TEST_DB = "test_smart_scheduler.db";
    
    @BeforeAll
    static void setup() {
        // Set test database name for Database class
        System.setProperty("db.name", TEST_DB);
        
        try {
            // Initialize test database
            Database.initialize();
        } catch (Exception e) {
            fail("Failed to initialize test database: " + e.getMessage());
        }
    }

    @AfterAll
    static void cleanup() {
        // Delete test database file
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            if (!dbFile.delete()) {
                System.err.println("Warning: Could not delete test database file");
            }
        }
    }

    @Test
    public void testDatabaseInitialization() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            
            // Check if all required tables exist
            String[] tables = {"users", "rooms", "courses", "timetable"};
            
            for (String table : tables) {
                try (ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "';"
                )) {
                    assertTrue(rs.next(), "Table '" + table + "' should exist");
                    assertEquals(table, rs.getString("name"), 
                        "Table name should match expected value");
                }
            }
        } catch (SQLException e) {
            fail("Database test failed: " + e.getMessage());
        }
    }

    @Test
    public void testTableStructure() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            
            // Verify users table structure
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(users)")) {
                assertTrue(rs.next());
                assertEquals("id", rs.getString("name"));
                assertTrue(rs.next());
                assertEquals("username", rs.getString("name"));
                assertTrue(rs.next());
                assertEquals("password", rs.getString("name"));
                assertTrue(rs.next());
                assertEquals("role", rs.getString("name"));
            }
            
        } catch (SQLException e) {
            fail("Table structure test failed: " + e.getMessage());
        }
    }
}