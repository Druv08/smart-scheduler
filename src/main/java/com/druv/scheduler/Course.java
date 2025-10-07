package com.druv.scheduler;

public class Course {
    private int id;
    private String courseCode;
    private String courseName;
    private String facultyUsername;
    private int maxStudents;

    public Course(int id, String courseCode, String courseName, String facultyUsername, int maxStudents) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.facultyUsername = facultyUsername;
        this.maxStudents = maxStudents;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getFacultyUsername() { return facultyUsername; }
    public void setFacultyUsername(String facultyUsername) { this.facultyUsername = facultyUsername; }
    
    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
}
