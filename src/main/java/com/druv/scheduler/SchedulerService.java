package com.druv.scheduler;

import java.util.List;

public class SchedulerService {
    private final UserDAO userDAO;
    private final RoomDAO roomDAO;
    private final CourseDAO courseDAO;

    public SchedulerService() {
        this.userDAO = new UserDAO();
        this.roomDAO = new RoomDAO();
        this.courseDAO = new CourseDAO();
    }

    // User methods
    public boolean addUser(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        String hashedPassword = Security.hashPassword(password);
        return userDAO.addUser(username, hashedPassword, role);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void showUsers() {
        List<User> users = getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\nAll Users:");
        System.out.println("----------------------------------------");
        System.out.printf("%-20s %-10s%n", "Username", "Role");
        System.out.println("----------------------------------------");
        
        for (User user : users) {
            System.out.printf("%-20s %-10s%n", 
                user.getUsername(), 
                user.getRole());
        }
        System.out.println("----------------------------------------");
    }

    // Room methods
    public boolean addRoom(String roomName, int capacity) {
        if (roomName == null || roomName.trim().isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        return roomDAO.addRoom(roomName, capacity);
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    public void showRooms() {
        List<Room> rooms = getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms available.");
            return;
        }

        System.out.println("\nAll Rooms:");
        System.out.println("----------------------------------------");
        System.out.printf("%-20s %-10s%n", "Room Name", "Capacity");
        System.out.println("----------------------------------------");
        
        for (Room room : rooms) {
            System.out.printf("%-20s %-10d%n", 
                room.getRoomName(), 
                room.getCapacity());
        }
        System.out.println("----------------------------------------");
        System.out.println("Total Rooms: " + rooms.size());
    }

    // Course management methods
    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
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

    private boolean isValidRole(String role) {
        return role != null && 
               (role.equals("student") || 
                role.equals("faculty") || 
                role.equals("admin"));
    }
}
