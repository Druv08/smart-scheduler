package com.druv.scheduler.ui;

import com.druv.scheduler.*;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final SchedulerService schedulerService;
    private User currentUser;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.schedulerService = new SchedulerService();
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
        // TODO: Implement login logic
        System.out.println("Login not implemented yet");
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
                    case 1 -> schedulerService.showUsers();
                    case 2 -> handleAddRoom();
                    case 3 -> schedulerService.showRooms();
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
        schedulerService.addRoom(name, capacity);
    }
}