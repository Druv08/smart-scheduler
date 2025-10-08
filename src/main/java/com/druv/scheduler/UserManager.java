package com.druv.scheduler;

import java.util.List;
import java.security.NoSuchAlgorithmException;

public class UserManager {
    private final UserDAO userDAO;

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public boolean addUser(String username, String password, String role) {
        try {
            String hashedPassword = HashUtil.hashPassword(password);
            return userDAO.addUser(username, hashedPassword, role.toUpperCase());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public User authenticate(String username, String password) {
        try {
            String hashedPassword = HashUtil.hashPassword(password);
            return userDAO.authenticate(username, hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}