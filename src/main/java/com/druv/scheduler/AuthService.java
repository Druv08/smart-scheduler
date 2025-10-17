package com.druv.scheduler;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDAO userDAO;
    private final Security security;

    @Autowired
    public AuthService(UserDAO userDAO, Security security) {
        this.userDAO = userDAO;
        this.security = security;
    }

    /**
     * Registers a new user with validation and secure password hashing.
     * @return Optional containing User if successful, empty if failed
     */
    public Optional<User> registerUser(String username, String password, String role) {
        // Input validation
        if (!isValidRegistrationInput(username, password, role)) {
            logger.warn("Invalid registration attempt: Invalid input parameters");
            return Optional.empty();
        }

        try {
            // Check if username already exists
            if (userDAO.getUserByUsername(username).isPresent()) {
                logger.warn("Registration failed: Username already exists - {}", username);
                return Optional.empty();
            }

            // Hash password and create user
            String hashedPassword = security.hashPassword(password);
            User newUser = userDAO.addUser(username, hashedPassword, role.toLowerCase());
            
            if (newUser != null) {
                logger.info("User registered successfully: {} with role {}", username, role);
                return Optional.of(newUser);
            }

            return Optional.empty();

        } catch (Exception e) {
            logger.error("Registration failed for user {}: {}", username, e.getMessage());
            throw new AuthenticationException("Registration failed", e);
        }
    }

    /**
     * Authenticates user credentials and creates a session.
     * @return Optional containing session token if successful
     */
    public Optional<String> login(String username, String password) {
        System.out.println("AuthService.login called with username: " + username);
        
        if (!isValidLoginInput(username, password)) {
            logger.warn("Invalid login attempt: Invalid credentials format");
            System.out.println("Login validation failed - invalid input format");
            return Optional.empty();
        }

        try {
            Optional<User> userOpt = userDAO.getUserByUsername(username);
            System.out.println("User found in database: " + userOpt.isPresent());
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (security.checkPassword(password, user.getPassword())) {
                    String sessionToken = security.createSession(user.getId(), user.getRole());
                    logger.info("Login successful for user: {}", username);
                    return Optional.of(sessionToken);
                }
            }

            logger.warn("Login failed for user: {}", username);
            return Optional.empty();

        } catch (Exception e) {
            logger.error("Login error for user {}: {}", username, e.getMessage());
            throw new AuthenticationException("Login failed", e);
        }
    }

    /**
     * Deletes a user by ID with proper authorization check.
     */
    public boolean deleteUser(int id, String sessionToken) {
        try {
            Security.Session session = security.getSession(sessionToken);
            if (session == null || !session.role().equals("admin")) {
                logger.warn("Unauthorized deletion attempt for user ID: {}", id);
                return false;
            }

            boolean deleted = userDAO.deleteUser(id);
            if (deleted) {
                logger.info("User deleted successfully: ID {}", id);
            } else {
                logger.warn("User not found for deletion: ID {}", id);
            }
            return deleted;

        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            throw new AuthenticationException("User deletion failed", e);
        }
    }

    /**
     * Lists all users with authorization check.
     */
    public List<User> getAllUsers(String sessionToken) {
        try {
            Security.Session session = security.getSession(sessionToken);
            if (session == null || !session.role().equals("admin")) {
                logger.warn("Unauthorized attempt to list users");
                throw new AuthorizationException("Unauthorized access to user list");
            }

            List<User> users = userDAO.getAllUsers();
            logger.debug("Retrieved {} users from database", users.size());
            return users;

        } catch (Exception e) {
            logger.error("Error retrieving user list: {}", e.getMessage());
            throw new AuthenticationException("Failed to retrieve users", e);
        }
    }

    private boolean isValidRegistrationInput(String username, String password, String role) {
        return username != null && !username.isBlank() &&
               password != null && password.length() >= 8 &&
               role != null && role.matches("(?i)student|faculty|admin");
    }

    private boolean isValidLoginInput(String username, String password) {
        return username != null && !username.isBlank() &&
               password != null && !password.isBlank();
    }
}
