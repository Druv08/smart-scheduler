package com.druv.scheduler;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserDAO userDAO;
    private final CourseDAO courseDAO;
    private final RoomDAO roomDAO;
    
    public DataInitializer(UserDAO userDAO, CourseDAO courseDAO, RoomDAO roomDAO) {
        this.userDAO = userDAO;
        this.courseDAO = courseDAO;
        this.roomDAO = roomDAO;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing test data...");
        
        // Add some test rooms
        if (roomDAO.findAll().isEmpty()) {
            roomDAO.addRoom("Room 101", 30);
            roomDAO.addRoom("Room 102", 25);
            roomDAO.addRoom("Lab A", 20);
            roomDAO.addRoom("Auditorium", 100);
            System.out.println("Added test rooms");
        }
        
        // Add some test courses
        if (courseDAO.findAll().isEmpty()) {
            courseDAO.addCourse("CS101", "Introduction to Computer Science", "admin", 30);
            courseDAO.addCourse("CS201", "Data Structures", "admin", 25);
            courseDAO.addCourse("CS301", "Database Systems", "admin", 20);
            System.out.println("Added test courses");
        }
        
        // Verify admin user exists
        Optional<User> admin = userDAO.getUserByUsername("admin");
        if (admin.isPresent()) {
            System.out.println("Admin user exists: " + admin.get().getUsername() + " (role: " + admin.get().getRole() + ")");
        }
        
        System.out.println("Data initialization completed");
        
        // Print some stats
        System.out.println("Total users: " + userDAO.getUserCount());
        System.out.println("Total courses: " + courseDAO.getCourseCount());
        System.out.println("Total rooms: " + roomDAO.getRoomCount());
    }
}
