package com.druv.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchedulerService {
    private final UserDAO userDAO;

    public SchedulerService() {
        this.userDAO = new UserDAO();
    }

    public void addRoom(String name, int capacity) {
        String sql = "INSERT INTO rooms (room_name, capacity) VALUES (?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, capacity);
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Added room: " + name);
            }
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
        }
    }

    public void showRooms() {
        String sql = "SELECT * FROM rooms";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                System.out.printf("Room %d: %s (Capacity: %d)%n",
                    rs.getInt("id"),
                    rs.getString("room_name"),
                    rs.getInt("capacity")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }
    }

    public void showUsers() {
        String sql = "SELECT * FROM users";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                System.out.printf("User %d: %s (%s)%n",
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }

    public void showTimetable() {
        String sql = """
            SELECT t.*, r.room_name, c.course_name 
            FROM timetable t
            JOIN rooms r ON t.room_id = r.id
            JOIN courses c ON t.course_id = c.id
            ORDER BY t.day_of_week, t.start_time
            """;
            
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                System.out.printf("%s: %s in %s (%s-%s)%n",
                    rs.getString("day_of_week"),
                    rs.getString("course_name"),
                    rs.getString("room_name"),
                    rs.getString("start_time"),
                    rs.getString("end_time")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching timetable: " + e.getMessage());
        }
    }

    // Change return type from void to boolean
    public boolean addUser(String username, String password, String role) {
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        // Hash password and delegate to DAO
        String hashedPassword = Security.hashPassword(password);
        return userDAO.addUser(username, hashedPassword, role);
    }

    private boolean isValidRole(String role) {
        return role != null && (role.equals("student") || 
                              role.equals("faculty") || 
                              role.equals("admin"));
    }
}
