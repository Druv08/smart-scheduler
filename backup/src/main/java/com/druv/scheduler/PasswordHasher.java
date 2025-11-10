package com.druv.scheduler;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static void main(String[] args) {
        String password = "admin123";
        
        // Generate a new hash
        String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("New BCrypt hash for 'admin123': " + newHash);
        
        // Test the new hash
        boolean newMatches = BCrypt.checkpw(password, newHash);
        System.out.println("New hash verification: " + newMatches);
        
        // Test the current database hash
        String currentHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye/EDzfeDb6rQHgwChek1gteNvk6Gj9P6";
        boolean currentMatches = BCrypt.checkpw(password, currentHash);
        System.out.println("Current database hash verification: " + currentMatches);
        
        // Also test with "password"
        boolean passwordMatches = BCrypt.checkpw("password", currentHash);
        System.out.println("Database hash matches 'password': " + passwordMatches);
    }
}