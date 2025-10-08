package com.druv.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        try {
            // Initialize database
            Database.initialize();
            
            // Start Spring Boot application
            SpringApplication.run(Main.class, args);
            
            System.out.println("Server started at http://localhost:8080");
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

