package com.druv.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SecurityTest {
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "wrong";
    
    @Test
    void testPasswordHashing() {
        String hashedPassword = Security.hashPassword(VALID_PASSWORD);
        assertTrue(Security.checkPassword(VALID_PASSWORD, hashedPassword));
        assertFalse(Security.checkPassword(INVALID_PASSWORD, hashedPassword));
    }
    
    @Test
    void testSessionManagement() {
        String token = Security.createSession(1, "admin");
        assertNotNull(token);
        
        Security.Session session = Security.getSession(token);
        assertNotNull(session);
        assertEquals(1, session.userId());
        assertEquals("admin", session.role());
        
        Security.invalidate(token);
        assertNull(Security.getSession(token));
    }
    
    @Test
    void testInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> 
            Security.hashPassword("short"));
        assertThrows(IllegalArgumentException.class, () -> 
            Security.createSession(0, "admin"));
        assertThrows(IllegalArgumentException.class, () -> 
            Security.createSession(1, ""));
    }
}