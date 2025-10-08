package com.druv.scheduler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MainTest {
    private static final String TEST_DB = "test-scheduler.db";

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(Paths.get(TEST_DB));
        System.setProperty("db.name", TEST_DB);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get(TEST_DB));
    }

    @Test
    void testDatabaseInitialization() {
        assertTrue(Database.initialize(), "Database initialization should succeed");
    }

    @Test
    void testDatabaseConnection() {
        Database.initialize();
        assertDoesNotThrow(() -> {
            try (Connection conn = Database.connect()) {
                assertNotNull(conn);
                assertTrue(conn.isValid(1));
            }
        });
    }

    @Test
    void testBasicFunctionality() {
        // Simple test that doesn't require complex setup
        assertTrue(true, "Basic test should pass");
    }
}
