package com.druv.scheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static String dbUrl = "jdbc:sqlite:scheduler.db";
    private static boolean initialized = false;
    private static List<Connection> activeConnections = new ArrayList<>();

    public static void setTestMode(String testDbName) {
        dbUrl = "jdbc:sqlite:" + testDbName;
        initialized = false;
        closeAll();
    }

    // Get a connection to the SQLite DB
    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        activeConnections.add(conn);
        return conn;
    }

    public static void closeAll() {
        for (Connection conn : activeConnections) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Ignore errors during cleanup
            }
        }
        activeConnections.clear();
    }

    // Read schema.sql from resources and create tables
    public static boolean initialize() {
        if (initialized) {
            System.out.println("Database already exists - skipping initialization.");
            return true;
        }

        try {
            Class.forName("org.sqlite.JDBC");
            
            try (Connection conn = connect();
                 Statement stmt = conn.createStatement()) {
                
                // Create tables
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL CHECK(role IN ('ADMIN', 'FACULTY', 'STUDENT'))
                    )""");
                    
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS rooms (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        room_name TEXT UNIQUE NOT NULL,
                        capacity INTEGER NOT NULL CHECK(capacity > 0)
                    )""");
                    
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS courses (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        course_code TEXT UNIQUE NOT NULL,
                        course_name TEXT NOT NULL,
                        faculty_username TEXT NOT NULL,
                        max_students INTEGER NOT NULL CHECK(max_students > 0),
                        FOREIGN KEY(faculty_username) REFERENCES users(username)
                    )""");
                    
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS timetable (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        course_id INTEGER NOT NULL,
                        room_id INTEGER NOT NULL,
                        day_of_week TEXT NOT NULL,
                        start_time TEXT NOT NULL,
                        end_time TEXT NOT NULL,
                        FOREIGN KEY(course_id) REFERENCES courses(id),
                        FOREIGN KEY(room_id) REFERENCES rooms(id),
                        UNIQUE(room_id, day_of_week, start_time, end_time)
                    )""");

                // Create default users if they don't exist
                createDefaultUsers(stmt);
                
                initialized = true;
                System.out.println("Database initialized successfully.");
                return true;
            }
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            return false;
        }
    }
    
    private static void createDefaultUsers(java.sql.Statement stmt) throws java.sql.SQLException {
        // Check if any users exist
        java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        int userCount = rs.getInt(1);
        rs.close();
        
        // Only create default users if no users exist
        if (userCount == 0) {
            System.out.println("Creating default test users...");
            
            // Create admin user
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('admin', " +
                "'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN')"); // password: password
            
            // Create faculty user
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('dr.prince', " +
                "'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'FACULTY')"); // password: password
            
            // Create student user  
            stmt.execute("INSERT INTO users (username, password, role) VALUES ('druv', " +
                "'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'STUDENT')"); // password: password
            
            System.out.println("Default users created:");
            System.out.println("- admin / password (ADMIN)");
            System.out.println("- dr.prince / password (FACULTY)"); 
            System.out.println("- druv / password (STUDENT)");
        }
    }
}

