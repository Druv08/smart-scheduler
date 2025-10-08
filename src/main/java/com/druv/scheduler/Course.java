package com.druv.scheduler;

public class Course {
    private int id;
    private String courseCode;
    private String courseName;
    private String facultyUsername;
    private int maxStudents;

    public Course(String code, String name, String facultyUsername, int maxStudents) {
        this.courseCode = code;
        this.courseName = name;
        this.facultyUsername = facultyUsername;
        this.maxStudents = maxStudents;
    }

    // Getters
    public int getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getFacultyUsername() { return facultyUsername; }
    public int getMaxStudents() { return maxStudents; }

    // Setters
    public void setId(int id) { this.id = id; }
}
