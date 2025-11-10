package com.druv.scheduler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Handles security operations including session management and password hashing.
 */
public class Security {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final int MIN_PASSWORD_LENGTH = 8;

    public String hashPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(password, hashedPassword);
    }

    public String createSession(int userId, String role) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }

        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(userId, role.trim()));
        return token;
    }

    public Session getSession(String token) {
        return sessions.get(token);
    }

    public void invalidateSession(String token) {
        sessions.remove(token);
    }

    public record Session(int userId, String role) {}
}


