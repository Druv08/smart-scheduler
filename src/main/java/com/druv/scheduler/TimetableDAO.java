package com.druv.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {
    private static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS timetable (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            course_id INTEGER NOT NULL,
            room_id INTEGER NOT NULL,
            day_of_week TEXT NOT NULL,
            start_time TEXT NOT NULL,
            end_time TEXT NOT NULL,
            FOREIGN KEY (course_id) REFERENCES courses(id),
            FOREIGN KEY (room_id) REFERENCES rooms(id),
            UNIQUE(room_id, day_of_week, start_time, end_time)
        )""";

    public TimetableDAO() {
        initializeTable();
    }

    private void initializeTable() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize timetable", e);
        }
    }

    public List<TimetableEntry> findAll() {
        List<TimetableEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM timetable";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                entries.add(new TimetableEntry(
                    rs.getInt("course_id"),
                    rs.getInt("room_id"),
                    rs.getString("day_of_week"),
                    rs.getString("start_time"),
                    rs.getString("end_time")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bookings", e);
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
    
    public boolean addTimetableEntry(TimetableEntry entry) {
        return addBooking(entry.getCourseId(), entry.getRoomId(), entry.getDayOfWeek(), 
                         entry.getStartTime(), entry.getEndTime());
    }
    
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
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching entries by room and day", e);
        }
        return entries;
    }
    
    public List<TimetableEntry> getByInstructorAndDay(int instructorId, String day) {
        List<TimetableEntry> entries = new ArrayList<>();
        String sql = """
            SELECT t.* FROM timetable t 
            JOIN courses c ON t.course_id = c.id 
            JOIN users u ON c.faculty_username = u.username 
            WHERE u.id = ? AND t.day_of_week = ?""";

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
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching entries by instructor and day", e);
        }
        return entries;
    }
}
