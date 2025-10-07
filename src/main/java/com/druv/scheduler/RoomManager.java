package com.druv.scheduler;

import java.util.List;

public class RoomManager {
    private final RoomDAO roomDAO = new RoomDAO();

    public boolean addRoom(String name, int capacity) {
        return roomDAO.addRoom(name, capacity);
    }

    public List<Room> listRooms() {
        return roomDAO.getAllRooms();
    }

    public boolean deleteRoom(int id) {
        return roomDAO.deleteRoom(id);
    }
}
