package com.druv.scheduler;

public class Room {
    private int id;
    private String name;
    private int capacity;

    public Room(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }

    // Setters
    public void setId(int id) { this.id = id; }
}
