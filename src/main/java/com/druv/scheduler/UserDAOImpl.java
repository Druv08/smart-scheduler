package com.druv.scheduler;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
<<<<<<< Updated upstream
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
=======
import org.springframework.stereotype.Repository;
>>>>>>> Stashed changes

@Repository
public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(String username, String hashedPassword, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, username, hashedPassword, role);
            return getUserByUsername(username).orElse(null);
        } catch (Exception e) {
            logger.error("Error adding user: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, 
                (rs, rowNum) -> Optional.of(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                )), username);
        } catch (Exception e) {
            logger.debug("User not found: {}", username);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> Optional.of(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                )), id);
        } catch (Exception e) {
            logger.debug("User not found: ID {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
            ));
    }

    @Override
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
        return jdbcTemplate.update(sql, 
            user.getUsername(), 
            user.getPassword(), 
            user.getRole(), 
            user.getId()) > 0;
    }

    @Override
    public long getUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
