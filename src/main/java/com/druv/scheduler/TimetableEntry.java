package com.druv.scheduler;

public class TimetableEntry {
    private int id;
    private int courseId;
    private int roomId;
    private int instructorId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    // Default constructor
    public TimetableEntry() {
    }

    public TimetableEntry(int courseId, int roomId, String dayOfWeek, String startTime, String endTime) {
        this.courseId = courseId;
        this.roomId = roomId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public int getRoomId() { return roomId; }
    public int getInstructorId() { return instructorId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public void setDay(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
