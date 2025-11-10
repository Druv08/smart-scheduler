package com.druv.scheduler;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User addUser(String username, String hashedPassword, String role);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(int id);
    List<User> getAllUsers();
    boolean deleteUser(int id);
    boolean updateUser(User user);
    long getUserCount();
}
