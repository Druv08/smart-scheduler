package com.druv.scheduler;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SMART SCHEDULER INITIALIZATION ===");
        
        try {
            // Initialize database
            Database.initialize();
            
            // Create scheduler service
            SchedulerService schedulerService = new SchedulerService();
            
            // Add sample users
            System.out.println("Adding sample users...");
            addSampleUsers(schedulerService);
            
            // Add sample rooms
            System.out.println("Adding sample rooms...");
            addSampleRooms(schedulerService);
            
            // Add sample courses
            System.out.println("Adding sample courses...");
            addSampleCourses(schedulerService);
            
            // Start web server
            System.out.println("Starting web server...");
            WebServer.start(schedulerService);
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down server...");
                WebServer.stop();
            }));
            
            // Keep main thread alive
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void addSampleUsers(SchedulerService service) {
        try {
            service.addUser("druv", "password123", "student");
            service.addUser("carlyn", "password123", "student");
            service.addUser("amrita", "password123", "student");
            service.addUser("allan", "password123", "student");
            service.addUser("dr.prince", "faculty123", "faculty");
            service.addUser("maam", "faculty123", "faculty");
            service.addUser("boss", "admin123", "admin");
        } catch (Exception e) {
            System.err.println("Error adding sample users: " + e.getMessage());
        }
    }

    private static void addSampleRooms(SchedulerService service) {
        try {
            service.addRoom("Lab 1", 30);
            service.addRoom("Lab 2", 30);
            service.addRoom("Classroom 101", 60);
            service.addRoom("Conference Room", 20);
        } catch (Exception e) {
            System.err.println("Error adding sample rooms: " + e.getMessage());
        }
    }

    private static void addSampleCourses(SchedulerService service) {
        try {
            service.addCourse("21CSC203P", "Advanced Programming Practices", "dr.prince", 150);
            service.addCourse("21CSC201J", "Data Structures and Algorithms", "maam", 100);
            service.addCourse("21CSC202J", "Operating Systems", "maam", 80);
            System.out.println("Sample courses added successfully");
        } catch (Exception e) {
            System.err.println("Error adding sample courses: " + e.getMessage());
        }
    }
}

