package com.druv.scheduler;

public class TimetableEntry {
    private int id;
    private int courseId;
    private int roomId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;

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
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    // Setters
    public void setId(int id) { this.id = id; }
}
