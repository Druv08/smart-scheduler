package com.druv.scheduler;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private final UserDAO userDAO;

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean addUser(String username, String password, String role) {
        try {
            String hashedPassword = HashUtil.hashPassword(password);
            User user = userDAO.addUser(username, hashedPassword, role.toUpperCase());
            return user != null;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public User authenticate(String username, String password) {
        Optional<User> userOpt = userDAO.getUserByUsername(username);
        if (userOpt.isPresent() && HashUtil.verifyPassword(password, userOpt.get().getPassword())) {
            return userOpt.get();
        }
        return null;
    }
}