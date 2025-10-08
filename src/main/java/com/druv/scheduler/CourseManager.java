package com.druv.scheduler;

import java.util.List;

public class CourseManager {
    private final CourseDAO courseDAO;

    public CourseManager(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    public boolean addCourse(String code, String name, String faculty, int maxStudents) {
        return courseDAO.addCourse(code, name, faculty, maxStudents);
    }

    public Course findByCode(String code) {
        return courseDAO.findByCode(code);
    }
}
