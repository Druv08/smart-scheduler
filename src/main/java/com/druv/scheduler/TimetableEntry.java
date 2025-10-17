package com.druv.scheduler;

public class TimetableEntry {
    private int id;
    private int courseId;
    private int roomId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private Integer instructorId; // Enhanced with instructor tracking

    // Default constructor for flexibility
    public TimetableEntry() {
    }

    public TimetableEntry(int courseId, int roomId, String dayOfWeek, String startTime, String endTime) {
        this.courseId = courseId;
        this.roomId = roomId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Enhanced constructor with instructor
    public TimetableEntry(int courseId, int roomId, String dayOfWeek, String startTime, String endTime, Integer instructorId) {
        this.courseId = courseId;
        this.roomId = roomId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instructorId = instructorId;
    }

    // Getters
    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public int getRoomId() { return roomId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public Integer getInstructorId() { return instructorId; }

    // Alias for getDayOfWeek for compatibility
    public String getDay() { return dayOfWeek; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId != null ? courseId : 0; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId != null ? roomId : 0; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setDay(String dayOfWeek) { this.dayOfWeek = dayOfWeek; } // Alias
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    @Override
    public String toString() {
        return "TimetableEntry{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", roomId=" + roomId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", instructorId=" + instructorId +
                '}';
    }
}
