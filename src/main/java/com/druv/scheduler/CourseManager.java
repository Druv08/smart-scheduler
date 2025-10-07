package com.druv.scheduler;

import java.util.List;

public class CourseManager {
    private final CourseDAO courseDAO;

    public CourseManager() {
        this.courseDAO = new CourseDAO();
    }

    public boolean addCourse(String courseCode, String courseName, String facultyUsername, int maxStudents) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        if (facultyUsername == null || facultyUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty username cannot be empty");
        }
        if (maxStudents <= 0) {
            throw new IllegalArgumentException("Maximum students must be positive");
        }

        return courseDAO.addCourse(courseCode, courseName, facultyUsername, maxStudents);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    public boolean deleteCourse(int courseId) {
        if (courseId <= 0) {
            throw new IllegalArgumentException("Invalid course ID");
        }
        return courseDAO.deleteCourse(courseId);
    }
}
