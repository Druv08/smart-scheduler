package com.druv.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SecurityTest {
    private Security security;
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrong";
    
    @BeforeEach
    void setUp() {
        security = new Security();
    }
    
    @Test
    void testPasswordHashing() {
        String hashedPassword = security.hashPassword(VALID_PASSWORD);
        assertNotNull(hashedPassword);
        assertTrue(security.checkPassword(VALID_PASSWORD, hashedPassword));
        assertFalse(security.checkPassword(INVALID_PASSWORD, hashedPassword));
    }
    
    @Test
    void testSessionManagement() {
        String token = security.createSession(1, "admin");
        assertNotNull(token);
        
        Security.Session session = security.getSession(token);
        assertNotNull(session);
        assertEquals(1, session.userId());
        assertEquals("admin", session.role());
        
        security.invalidateSession(token);
        assertNull(security.getSession(token));
    }
    
    @Test
    void testInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> 
            security.hashPassword("short"));
        assertThrows(IllegalArgumentException.class, () -> 
            security.createSession(0, "admin"));
        assertThrows(IllegalArgumentException.class, () -> 
            security.createSession(1, ""));
    }
}