package com.druv.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS rooms (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            room_name TEXT UNIQUE NOT NULL,
            capacity INTEGER NOT NULL CHECK(capacity > 0)
        )""";

    public RoomDAO() {
        initializeTable();
    }

    private void initializeTable() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize rooms table", e);
        }
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_name"),
                    rs.getInt("capacity")
                );
                room.setId(rs.getInt("id"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching rooms", e);
        }
        return rooms;
    }

    public boolean addRoom(String name, int capacity) {
        String sql = "INSERT INTO rooms (room_name, capacity) VALUES (?, ?)";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setInt(2, capacity);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                return false; // Room name already exists
            }
            throw new RuntimeException("Error adding room", e);
        }
    }

    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room(
                        rs.getString("room_name"),
                        rs.getInt("capacity")
                    );
                    room.setId(rs.getInt("id"));
                    return room;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding room by id", e);
        }
        return null;
    }

    public Room findByName(String name) {
        String sql = "SELECT * FROM rooms WHERE room_name = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room(
                        rs.getString("room_name"),
                        rs.getInt("capacity")
                    );
                    room.setId(rs.getInt("id"));
                    return room;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding room", e);
        }
        return null;
    }

    public boolean updateRoom(int id, String name, int capacity) {
        String sql = "UPDATE rooms SET room_name = ?, capacity = ? WHERE id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setInt(2, capacity);
            stmt.setInt(3, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating room", e);
        }
    }

    public boolean updateRoom(Room room) {
        return updateRoom(room.getId(), room.getName(), room.getCapacity());
    }

    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting room", e);
        }
    }

    public long getRoomCount() {
        String sql = "SELECT COUNT(*) FROM rooms";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting room count", e);
        }
    }
}
