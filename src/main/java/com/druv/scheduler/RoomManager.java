package com.druv.scheduler;

import java.util.List;

public class RoomManager {
    private final RoomDAO roomDAO;

    public RoomManager(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    public boolean addRoom(String name, int capacity) {
        return roomDAO.addRoom(name, capacity);
    }

    public Room findByName(String name) {
        return roomDAO.findByName(name);
    }
}
