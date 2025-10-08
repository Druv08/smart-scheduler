package com.druv.scheduler;

import java.util.List;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Registers a new user only if the username is not already taken and the role is valid.
     */
    public boolean registerUser(String username, String password, String role) {
        if (username == null || password == null || role == null ||
            username.isBlank() || password.isBlank() || !role.matches("(?i)student|faculty|admin")) {
            System.out.println("Invalid registration data");
            return false;
        }
        
        try {
            String hashed = Security.hashPassword(password);
            boolean success = userDAO.addUser(username, hashed, role.toLowerCase());
            if (success) {
                System.out.println("Registered user: " + username + " as " + role);
            } else {
                System.out.println("Username already exists: " + username);
            }
            return success;
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates login credentials.
     */
    public User login(String username, String password) {
        if (username == null || password == null || 
            username.isBlank() || password.isBlank()) {
            System.out.println("Invalid login credentials");
            return null;
        }

        try {
            List<User> users = userDAO.getAllUsers();
            String hashed = Security.hashPassword(password);
            
            for (User user : users) {
                if (user.getUsername().equals(username) && 
                    user.getPassword().equals(hashed)) {
                    System.out.println("Login successful: " + username);
                    return user;
                }
            }
            
            System.out.println("Login failed: " + username);
            return null;
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(int id) {
        try {
            userDAO.deleteUser(id);
            System.out.println("Deleted user with ID: " + id);
        } catch (Exception e) {
            System.err.println("Failed to delete user: " + e.getMessage());
        }
    }

    /**
     * Lists all users.
     */
    public void listAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found");
                return;
            }
            
            users.forEach(user -> 
                System.out.println("User " + user.getId() + ": " + 
                    user.getUsername() + " (" + user.getRole() + ")")
            );
        } catch (Exception e) {
            System.err.println("Failed to list users: " + e.getMessage());
        }
    }
}
