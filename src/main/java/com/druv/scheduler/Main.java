package com.druv.scheduler;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SMART SCHEDULER INITIALIZATION ===");

        // Initialize schema
        Database.initialize();

        // Clear existing users
        DatabaseManager.clearUsers();

        // Add sample users
        System.out.println("\nAdding sample users...");
        DatabaseManager.addUser("alice", "password123", "student");
        DatabaseManager.addUser("bob", "adminpass", "admin");
        DatabaseManager.addUser("carol", "teachpass", "faculty");

        // Show users
        System.out.println("\nFetching all users:");
        DatabaseManager.fetchAllUsers();

        // Delete one user
        System.out.println("\nDeleting user 'bob'...");
        DatabaseManager.deleteUser("bob");

        // Show users again
        System.out.println("\nRemaining users:");
        DatabaseManager.fetchAllUsers();
    }
}

