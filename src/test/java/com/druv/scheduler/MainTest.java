package com.druv.scheduler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MainTest {
    private SchedulerService service;
    private static final String TEST_TOKEN = "test-admin-token";
    private static final String TEST_DB = "test-scheduler.db";

    @BeforeEach
    void setUp() throws Exception {
        // Clean up any existing test database
        Files.deleteIfExists(Paths.get(TEST_DB));
        
        // Use test database
        Database.setTestMode(TEST_DB);
        assertTrue(Database.initialize(), "Database initialization failed");
        
        // Initialize service with DAOs
        UserDAO userDAO = new UserDAO();
        CourseDAO courseDAO = new CourseDAO();
        RoomDAO roomDAO = new RoomDAO();
        TimetableDAO timetableDAO = new TimetableDAO();
        service = new SchedulerService(userDAO, courseDAO, roomDAO, timetableDAO);
        
        // Create admin user directly through DAO to bootstrap
        String hashedPassword = HashUtil.hashPassword("admin123");
        assertTrue(
            userDAO.addUser("admin", hashedPassword, "ADMIN"),
            "Failed to create admin user"
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        service = null;
        // Close connections
        Database.closeAll();
        // Delete test database
        Files.deleteIfExists(Paths.get(TEST_DB));
    }

    @Test
    void testMainStartup() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }

    @Test
    void testDatabaseInitialization() {
        assertTrue(Database.initialize(), "Database initialization should succeed");
        
        assertDoesNotThrow(() -> {
            try (Connection conn = Database.connect()) {
                assertNotNull(conn, "Database connection should not be null");
                assertFalse(conn.isClosed(), "Database connection should be open");
            }
        });
    }

    @Test
    void testUserManagement() {
        // Login as admin first
        Map<String, Object> loginResult = service.login("admin", "admin123");
        assertTrue((boolean)loginResult.get("success"));
        String token = (String)loginResult.get("token");

        // Add new user
        assertTrue(
            service.addUser(token, "testuser", "password123", "STUDENT"),
            "Should add valid user"
        );

        // Try duplicate
        assertFalse(
            service.addUser(token, "testuser", "password123", "STUDENT"),
            "Should reject duplicate username"
        );
    }

    @Test
    void testRoomManagement() {
        // Login as admin first
        Map<String, Object> loginResult = service.login("admin", "admin123");
        String token = (String)loginResult.get("token");

        assertTrue(
            service.addRoom(token, "Room101", 30),
            "Should add valid room"
        );
        
        assertFalse(
            service.addRoom(token, "Room101", 40),
            "Should reject duplicate room"
        );
    }

    @Test
    void testCourseManagement() {
        // Login and create faculty user first
        Map<String, Object> loginResult = service.login("admin", "admin123");
        String token = (String)loginResult.get("token");
        
        service.addUser(token, "prof1", "prof123", "FACULTY");

        assertTrue(
            service.addCourse(token, "CS101", "Programming", "prof1", 30),
            "Should add valid course"
        );
        
        assertFalse(
            service.addCourse(token, "CS101", "Programming", "prof2", 40),
            "Should reject duplicate course"
        );
    }

    @Test
    void testAuthentication() {
        // Admin user was created in setUp()
        Map<String, Object> result = service.login("admin", "admin123");
        assertTrue((boolean)result.get("success"), "Valid login should succeed");
        assertNotNull(result.get("token"), "Login should return auth token");
    }

    @Test
    void testInvalidAuthentication() {
        Map<String, Object> result = service.login("baduser", "badpass");
        assertFalse((boolean)result.get("success"), "Invalid login should fail");
        assertNull(result.get("token"), "Failed login should not return token");
    }
}
