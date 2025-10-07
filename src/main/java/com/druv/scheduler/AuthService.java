package com.druv.scheduler;

import java.util.List;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Registers a new user only if the username is not already taken and the role is valid.
     */
    public boolean registerUser(String username, String password, String role) {
        if (!role.matches("student|faculty|admin")) {
            System.out.println("Invalid role: " + role);
            return false;
        }
        String hashed = Security.hashPassword(password);
        boolean success = userDAO.addUser(username, hashed, role);
        if (success) {
            System.out.println("Registered user: " + username + " as " + role);
        }
        return success;
    }

    /**
     * Validates login credentials.
     */
    public User login(String username, String password) {
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && Security.checkPassword(password, user.getPassword())) {
                System.out.println("Login successful: " + username);
                return user;
            }
        }
        System.out.println("Login failed: " + username);
        return null;
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(int id) {
        userDAO.deleteUser(id);
        System.out.println("Deleted user with ID: " + id);
    }

    /**
     * Lists all users.
     */
    public void listAllUsers() {
        List<User> users = userDAO.getAllUsers();
        users.forEach(user -> 
            System.out.println("User " + user.getId() + ": " + user.getUsername() + " (" + user.getRole() + ")")
        );
    }
}
