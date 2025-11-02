package com.druv.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS courses (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            course_code TEXT UNIQUE NOT NULL,
            course_name TEXT NOT NULL,
            faculty_username TEXT NOT NULL,
            max_students INTEGER NOT NULL,
            FOREIGN KEY (faculty_username) REFERENCES users(username)
        )""";

    public CourseDAO() {
        initializeTable();
    }

    private void initializeTable() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize courses table", e);
        }
    }

    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = new Course(
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getString("faculty_username"),
                    rs.getInt("max_students")
                );
                course.setId(rs.getInt("id"));
                courses.add(course);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching courses", e);
        }
        return courses;
    }

    public boolean addCourse(Course course) {
        String sql = """
            INSERT INTO courses (course_code, course_name, faculty_username, max_students) 
            VALUES (?, ?, ?, ?)""";
            
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getFacultyUsername());  // Changed from getFaculty()
            stmt.setInt(4, course.getMaxStudents());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                return false; // Course code already exists
            }
            throw new RuntimeException("Error adding course", e);
        }
    }

    public boolean addCourse(String code, String name, String faculty, int maxStudents) {
        String sql = "INSERT INTO courses (course_code, course_name, faculty_username, max_students) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setString(3, faculty);
            stmt.setInt(4, maxStudents);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                return false; // Course code already exists
            }
            throw new RuntimeException("Error adding course", e);
        }
    }

    public Course findById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course(
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("faculty_username"),
                        rs.getInt("max_students")
                    );
                    course.setId(rs.getInt("id"));
                    return course;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course by id", e);
        }
        return null;
    }

    public Course findByCode(String courseCode) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course(
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("faculty_username"),
                        rs.getInt("max_students")
                    );
                    course.setId(rs.getInt("id"));
                    return course;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course", e);
        }
        return null;
    }

    public boolean updateCourse(int id, String code, String name, String faculty, int maxStudents) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, faculty_username = ?, max_students = ? WHERE id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setString(3, faculty);
            stmt.setInt(4, maxStudents);
            stmt.setInt(5, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating course", e);
        }
    }

    public boolean updateCourse(Course course) {
        return updateCourse(course.getId(), course.getCourseCode(), course.getCourseName(),
                          course.getFacultyUsername(), course.getMaxStudents());
    }

    public boolean deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting course", e);
        }
    }

    public long getCourseCount() {
        String sql = "SELECT COUNT(*) FROM courses";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting course count", e);
        }
    }
}
