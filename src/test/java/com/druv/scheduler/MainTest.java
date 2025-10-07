package com.druv.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private SchedulerService service;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(outContent));
        service = new SchedulerService();
        Database.initialize(); // Ensure fresh database for each test
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testDatabaseInitialization() {
        outContent.reset();
        Database.initialize();
        String output = outContent.toString();
        assertTrue(output.contains("Database initialized successfully"), 
                  "Should show successful database initialization");
    }

    @Test
    void testUserManagement() {
        outContent.reset();
        assertDoesNotThrow(() -> {
            service.addUser("testuser", "password123", "student");
            String output = outContent.toString();
            assertTrue(output.contains("Added user: testuser"), 
                      "Should show user addition confirmation");
        }, "User addition should not throw exception");
    }

    @Test
    void testInvalidUserRole() {
        outContent.reset();
        assertThrows(IllegalArgumentException.class, () -> {
            service.addUser("baduser", "password123", "invalid_role");
        }, "Should reject invalid user role");
    }

    @Test
    void testDuplicateUsername() {
        // First attempt should succeed
        boolean firstResult = service.addUser("uniqueuser", "password123", "student");
        assertTrue(firstResult, "First user addition should succeed");
        assertTrue(outContent.toString().contains("Added user: uniqueuser"), 
                  "Should show success message for first user");
        
        // Reset output buffer
        outContent.reset();
        
        // Second attempt should fail
        boolean secondResult = service.addUser("uniqueuser", "password123", "student");
        assertFalse(secondResult, "Second attempt should fail");
        assertTrue(outContent.toString().contains("Username already exists"), 
                  "Should show duplicate username error");
    }

    @Test
    void testMainInitialization() {
        outContent.reset();
        assertDoesNotThrow(() -> {
            Main.main(new String[]{});
            String output = outContent.toString();
            
            assertAll("Main initialization sequence",
                () -> assertTrue(output.contains("=== SMART SCHEDULER INITIALIZATION ==="), 
                    "Should show initialization message"),
                () -> assertTrue(output.contains("Database initialized successfully"), 
                    "Should initialize database"),
                () -> assertTrue(output.contains("Adding sample users"), 
                    "Should add sample users"),
                () -> assertTrue(output.contains("Fetching all users"), 
                    "Should fetch users"),
                () -> assertTrue(output.contains("alice (student)"), 
                    "Should show student user"),
                () -> assertTrue(output.contains("carol (faculty)"), 
                    "Should show faculty user")
            );
        }, "Main initialization should not throw exception");
    }

    @Test
    void testEmptyUsername() {
        outContent.reset();
        assertThrows(IllegalArgumentException.class, () -> {
            service.addUser("", "password123", "student");
        }, "Should reject empty username");
    }

    @Test
    void testNullPassword() {
        outContent.reset();
        assertThrows(IllegalArgumentException.class, () -> {
            service.addUser("validuser", null, "student");
        }, "Should reject null password");
    }
}
