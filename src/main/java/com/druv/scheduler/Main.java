package com.druv.scheduler;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize database
            Database.initialize();
            
            // Initialize DAOs
            UserDAO userDAO = new UserDAO();
            RoomDAO roomDAO = new RoomDAO();
            CourseDAO courseDAO = new CourseDAO();
            TimetableDAO timetableDAO = new TimetableDAO();
            
            // Initialize managers
            UserManager userManager = new UserManager(userDAO);
            RoomManager roomManager = new RoomManager(roomDAO);
            CourseManager courseManager = new CourseManager(courseDAO);
            TimetableManager timetableManager = new TimetableManager(timetableDAO);
            
            // Start web server
            WebServer server = new WebServer(userManager, roomManager, courseManager, timetableManager);
            server.start();
            
            System.out.println("Server started at http://localhost:8080");
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

