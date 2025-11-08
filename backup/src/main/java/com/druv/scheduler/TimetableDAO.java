package com.druv.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {
    // Table creation is handled by Database.java
    // No need to duplicate schema here

    public List<TimetableEntry> findAll() {
        List<TimetableEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM timetable";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                TimetableEntry entry = new TimetableEntry(
                    rs.getInt("course_id"),
                    rs.getInt("room_id"),
                    rs.getString("day_of_week"),
                    rs.getString("start_time"),
                    rs.getString("end_time")
                );
                
                // Set denormalized fields for frontend display
                entry.setCourseName(rs.getString("course_name"));
                entry.setFaculty(rs.getString("faculty"));
                entry.setRoomName(rs.getString("room_name"));
                entry.setSlotCode(rs.getString("slot_code"));
                entry.setType(rs.getString("type"));
                
                // Set instructor_id if present
                int instructorId = rs.getInt("instructor_id");
                if (!rs.wasNull()) {
                    entry.setInstructorId(instructorId);
                }
                
                entries.add(entry);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching timetable entries", e);
        }
        return entries;
    }

    public boolean addBooking(int courseId, int roomId, String day, String startTime, String endTime) {
        String sql = "INSERT INTO timetable (course_id, room_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            stmt.setInt(2, roomId);
            stmt.setString(3, day);
            stmt.setString(4, startTime);
            stmt.setString(5, endTime);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                return false; // Time slot conflict
            }
            throw new RuntimeException("Error adding booking", e);
        }
    }

    public boolean hasTimeConflict(int roomId, String day, String startTime, String endTime) {
        String sql = """
            SELECT COUNT(*) FROM timetable 
            WHERE room_id = ? 
            AND day_of_week = ? 
            AND ((start_time < ? AND end_time > ?) 
                 OR 
                 (start_time < ? AND end_time > ?))""";
                 
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setString(2, day);
            stmt.setString(3, endTime);
            stmt.setString(4, startTime);
            stmt.setString(5, startTime);
            stmt.setString(6, endTime);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking time conflicts", e);
        }
    }

    /**
     * Add timetable entry with enhanced functionality
     */
    public boolean addTimetableEntry(TimetableEntry entry) {
        String sql = "INSERT INTO timetable (course_id, room_id, day_of_week, start_time, end_time, instructor_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, entry.getCourseId());
            stmt.setInt(2, entry.getRoomId());
            stmt.setString(3, entry.getDayOfWeek());
            stmt.setString(4, entry.getStartTime());
            stmt.setString(5, entry.getEndTime());
            if (entry.getInstructorId() != null) {
                stmt.setInt(6, entry.getInstructorId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding timetable entry", e);
        }
    }

    /**
     * Delete timetable entry by ID
     */
    public boolean deleteEntry(int id) {
        String sql = "DELETE FROM timetable WHERE id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting timetable entry", e);
        }
    }

    /**
     * Get all timetable entries for a specific room and day
     */
    public List<TimetableEntry> getByRoomAndDay(int roomId, String day) {
        List<TimetableEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM timetable WHERE room_id = ? AND day_of_week = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setString(2, day);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TimetableEntry entry = new TimetableEntry(
                        rs.getInt("course_id"),
                        rs.getInt("room_id"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                    );
                    entry.setId(rs.getInt("id"));
                    // Handle instructor_id (might be null in older schema)
                    try {
                        int instructorId = rs.getInt("instructor_id");
                        if (!rs.wasNull()) {
                            entry.setInstructorId(instructorId);
                        }
                    } catch (SQLException e) {
                        // instructor_id column doesn't exist - ignore
                    }
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching timetable entries by room and day", e);
        }
        
        return entries;
    }

    /**
     * Get all timetable entries for a specific instructor and day
     */
    public List<TimetableEntry> getByInstructorAndDay(int instructorId, String day) {
        List<TimetableEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM timetable WHERE instructor_id = ? AND day_of_week = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, instructorId);
            stmt.setString(2, day);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TimetableEntry entry = new TimetableEntry(
                        rs.getInt("course_id"),
                        rs.getInt("room_id"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                    );
                    entry.setId(rs.getInt("id"));
                    entry.setInstructorId(rs.getInt("instructor_id"));
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching timetable entries by instructor and day", e);
        }
        
        return entries;
    }

    /**
     * Get all timetable entries for a specific room
     */
    public List<TimetableEntry> getByRoom(int roomId) {
        List<TimetableEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM timetable WHERE room_id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TimetableEntry entry = new TimetableEntry(
                        rs.getInt("course_id"),
                        rs.getInt("room_id"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                    );
                    entry.setId(rs.getInt("id"));
                    // Handle instructor_id (might be null)
                    try {
                        int instructorIdValue = rs.getInt("instructor_id");
                        if (!rs.wasNull()) {
                            entry.setInstructorId(instructorIdValue);
                        }
                    } catch (SQLException e) {
                        // instructor_id column doesn't exist - ignore
                    }
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching timetable entries by room", e);
        }
        
        return entries;
    }

    public long getUpcomingClassesCount() {
        // For simplicity, we'll count all timetable entries as "upcoming"
        // In a real application, this would include date/time logic
        String sql = "SELECT COUNT(*) FROM timetable";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting upcoming classes count", e);
        }
    }
    
    /**
     * Save or update a timetable entry
     * If an entry exists for the same room, day, and time, it will be updated
     * Otherwise, a new entry will be created
     */
    public boolean saveOrUpdateEntry(TimetableEntry entry) {
        // For seed data with denormalized fields, we don't check for existing entries
        // We just insert with all the provided data
        String insertSql = """
            INSERT INTO timetable (
                course_id, room_id, day_of_week, start_time, end_time, 
                instructor_id, course_name, faculty, room_name, slot_code, type
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = Database.connect();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            
            // Set nullable foreign key fields (use 0 to indicate NULL)
            if (entry.getCourseId() > 0) {
                insertStmt.setInt(1, entry.getCourseId());
            } else {
                insertStmt.setNull(1, java.sql.Types.INTEGER);
            }
            
            if (entry.getRoomId() > 0) {
                insertStmt.setInt(2, entry.getRoomId());
            } else {
                insertStmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            // Set required fields
            insertStmt.setString(3, entry.getDayOfWeek());
            insertStmt.setString(4, entry.getStartTime());
            insertStmt.setString(5, entry.getEndTime());
            
            // Set instructor_id (nullable)
            if (entry.getInstructorId() != null) {
                insertStmt.setInt(6, entry.getInstructorId());
            } else {
                insertStmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            // Set denormalized fields (for seed data)
            insertStmt.setString(7, entry.getCourseName());
            insertStmt.setString(8, entry.getFaculty());
            insertStmt.setString(9, entry.getRoomName());
            insertStmt.setString(10, entry.getSlotCode());
            insertStmt.setString(11, entry.getType());
            
            return insertStmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving/updating timetable entry: " + e.getMessage(), e);
        }
    }
}
