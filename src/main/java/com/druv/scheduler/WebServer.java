package com.druv.scheduler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebServer {
    
    @Autowired
    private ApiController apiController;

    public WebServer() {
        // Simple constructor for static file serving
    }

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/dashboard.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "forward:/login.html";
    }
    
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        // Delegate to the ApiController
        return apiController.login(credentials);
    }

    @GetMapping("/error")
    @ResponseBody
    public String error() {
        return "Page not found";
    }

    @GetMapping("/users")
    public String users() {
        return "forward:/users.html";
    }

    @GetMapping("/courses")
    public String courses() {
        return "forward:/courses.html";
    }

    @GetMapping("/rooms")
    public String rooms() {
        return "forward:/rooms.html";
    }

    @GetMapping("/timetable")
    public String timetable() {
        return "forward:/timetable.html";
    }

    @GetMapping("/bookings")
    public String bookings() {
        return "forward:/bookings.html";
    }

    @GetMapping("/profile-settings")
    public String profileSettings() {
        return "forward:/profile-settings.html";
    }

    @GetMapping("/scheduler-engine")
    public String schedulerEngine() {
        return "forward:/scheduler-engine.html";
    }

    @GetMapping("/scheduler-control")
    public String schedulerControl() {
        return "forward:/scheduler-control.html";
    }

    // Similar patterns for rooms, courses, and bookings routes
    // ...existing route handlers...
}
