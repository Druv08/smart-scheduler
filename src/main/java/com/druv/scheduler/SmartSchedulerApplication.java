package com.druv.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.druv.scheduler")
public class SmartSchedulerApplication {

    public static void main(String[] args) {
        // Initialize database before starting Spring Boot
        Database.initialize();
        
        SpringApplication.run(SmartSchedulerApplication.class, args);
    }
}