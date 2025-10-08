package com.druv.scheduler.ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.druv.scheduler.Course;
import com.druv.scheduler.Room;
import com.druv.scheduler.SchedulerService;
import com.druv.scheduler.User;

public class ConsoleUI {
    private final Scanner scanner;
    private final SchedulerService schedulerService;
    private User currentUser;
    private String authToken;  // Add this field to store the authentication token

    public ConsoleUI(SchedulerService schedulerService) {
        this.scanner = new Scanner(System.in);
        this.schedulerService = schedulerService;
    }

    public void start() {
        System.out.println("=== Smart Scheduler System ===");
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleRegister();
            case 3 -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid option!");
        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        Map<String, Object> result = schedulerService.login(username, password);
        
        if ((boolean)result.get("success")) {
            this.currentUser = (User)result.get("user");
            this.authToken = (String)result.get("token");
            System.out.println("Login successful!");
        } else {
            System.out.println("Login failed: " + result.get("error"));
        }
    }

    private void handleRegister() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Role (student/faculty/admin): ");
        String role = scanner.nextLine();
        // TODO: Implement registration logic
        System.out.println("Registration not implemented yet");
    }

    private void showMainMenu() {
        switch (currentUser.getRole()) {
            case "admin" -> showAdminMenu();
            case "faculty" -> showFacultyMenu();
            case "student" -> showStudentMenu();
            default -> {
                System.out.println("Invalid role!");
                currentUser = null;
            }
        }
    }

    private void showAdminMenu() {
        while (true) {  // Add loop to prevent auto-logout
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. List All Users");
            System.out.println("2. Add Room");
            System.out.println("3. List Rooms");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> showAllUsers();     // Changed from schedulerService.showUsers()
                    case 2 -> handleAddRoom();
                    case 3 -> showAllRooms();     // Changed from schedulerService.showRooms()
                    case 4 -> {
                        currentUser = null;
                        return;
                    }
                    default -> System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // clear bad input
            }
        }
    }

    private void showFacultyMenu() {
        // TODO: Implement faculty menu
        System.out.println("Faculty menu not implemented yet");
    }

    private void showStudentMenu() {
        // TODO: Implement student menu
        System.out.println("Student menu not implemented yet");
    }

    private void handleAddRoom() {
        System.out.print("Room name: ");
        String name = scanner.nextLine();
        System.out.print("Capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        if (schedulerService.addRoom(authToken, name, capacity)) {
            System.out.println("Room added successfully!");
        } else {
            System.out.println("Failed to add room.");
        }
    }

    private void showAllCourses() {
        System.out.println("\n=== COURSES ===");
        List<Course> courses = schedulerService.getAllCourses(authToken);
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }
        for (Course course : courses) {
            System.out.printf("Code: %s, Name: %s, Faculty: %s, Max Students: %d%n",
                course.getCourseCode(), course.getCourseName(),
                course.getFacultyUsername(), course.getMaxStudents());
        }
    }

    private void showAllUsers() {
        System.out.println("\n=== USERS ===");
        List<User> users = schedulerService.getAllUsers(authToken);
        if (users.isEmpty()) {
            System.out.println("No users available.");
            return;
        }
        for (User user : users) {
            System.out.printf("Username: %s, Role: %s%n", 
                user.getUsername(), user.getRole());
        }
    }

    private void showAllRooms() {
        System.out.println("\n=== ROOMS ===");
        List<Room> rooms = schedulerService.getAllRooms(authToken);
        if (rooms.isEmpty()) {
            System.out.println("No rooms available.");
            return;
        }
        for (Room room : rooms) {
            System.out.printf("Name: %s, Capacity: %d%n",
                room.getName(), room.getCapacity());
        }
    }

    private void addNewRoom() {
        System.out.print("Enter room name: ");
        String name = scanner.nextLine();
        System.out.print("Enter room capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (schedulerService.addRoom(authToken, name, capacity)) {
            System.out.println("Room added successfully!");
        } else {
            System.out.println("Failed to add room.");
        }
    }
}