package com.druv.scheduler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Handles security operations including session management and password hashing.
 */
public class Security {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final int SESSION_EXPIRY_MINUTES = 30;
    
    public record Session(int userId, String role, long createdAt) {
        public Session(int userId, String role) {
            this(userId, role, System.currentTimeMillis());
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - createdAt > SESSION_EXPIRY_MINUTES * 60 * 1000;
        }
    }
    
    public static String createSession(int userId, String role) {
        if (userId <= 0 || role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid user ID or role");
        }
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(userId, role));
        return token;
    }
    
    public static Session getSession(String token) {
        if (token == null) return null;
        Session session = sessions.get(token);
        if (session != null && session.isExpired()) {
            invalidate(token);
            return null;
        }
        return session;
    }
    
    public static void invalidate(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }
    
    public static String hashPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void cleanup() {
        sessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}


