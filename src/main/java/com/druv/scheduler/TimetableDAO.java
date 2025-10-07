package com.druv.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {
    public boolean addEntry(int courseId, int roomId, String dayOfWeek, String startTime, String endTime) {
        final String sql = "INSERT INTO timetable (course_id, room_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, roomId);
            ps.setString(3, dayOfWeek);
            ps.setString(4, startTime);
            ps.setString(5, endTime);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding timetable entry: " + e.getMessage());
            return false;
        }
    }

    public List<TimetableEntry> getAllEntries() {
        final String sql = "SELECT id, course_id, room_id, day_of_week, start_time, end_time FROM timetable ORDER BY day_of_week, start_time";
        List<TimetableEntry> entries = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                entries.add(new TimetableEntry(
                    rs.getInt("id"),
                    rs.getInt("course_id"),
                    rs.getInt("room_id"),
                    rs.getString("day_of_week"),
                    rs.getString("start_time"),
                    rs.getString("end_time")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching timetable: " + e.getMessage());
        }
        return entries;
    }

    public boolean deleteEntry(int id) {
        final String sql = "DELETE FROM timetable WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting timetable entry: " + e.getMessage());
            return false;
        }
    }
}
