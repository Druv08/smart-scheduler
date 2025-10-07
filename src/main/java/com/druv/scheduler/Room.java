package com.druv.scheduler;

public class Room {
    private int id;
    private String room_name;
    private int capacity;

    public Room(int id, String room_name, int capacity) {
        this.id = id;
        this.room_name = room_name;
        this.capacity = capacity;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomName() { return room_name; }
    public void setRoomName(String room_name) { this.room_name = room_name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
