package com.druv.scheduler;

public class TimetableEntry {
    private int id;
    private int courseId;
    private int roomId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public TimetableEntry(int id, int courseId, int roomId, String dayOfWeek, 
                         String startTime, String endTime) {
        this.id = id;
        this.courseId = courseId;
        this.roomId = roomId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public int getRoomId() { return roomId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    @Override
    public String toString() {
        return String.format("TimetableEntry{id=%d, courseId=%d, roomId=%d, day=%s, %s-%s}",
                id, courseId, roomId, dayOfWeek, startTime, endTime);
    }
}
