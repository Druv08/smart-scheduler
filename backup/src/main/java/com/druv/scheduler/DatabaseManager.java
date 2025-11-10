package com.druv.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    // Create a user
    public static void addUser(String username, String password, String role) {
        final String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            System.out.println("Added user: " + username);
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    // Read all users
    public static void fetchAllUsers() {
        final String sql = "SELECT id, username, role FROM users ORDER BY id";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                System.out.printf("User %d: %s (%s)%n",
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }

    // Delete by username
    public static void deleteUser(String username) {
        final String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            int affected = pstmt.executeUpdate();
            System.out.println("Deleted " + affected + " user(s)");
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    public static void clearUsers() {
        final String sql = "DELETE FROM users";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int affected = pstmt.executeUpdate();
            System.out.println("Cleared " + affected + " user(s)");
        } catch (SQLException e) {
            System.err.println("Error clearing users: " + e.getMessage());
        }
    }
}
