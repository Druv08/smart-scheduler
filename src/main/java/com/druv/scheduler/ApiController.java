package com.druv.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Component
public class ApiController {
    
    private final SchedulerService schedulerService;
    private final AuthService authService;
    private final UserDAO userDAO;
    
    public ApiController(SchedulerService schedulerService, AuthService authService, UserDAO userDAO) {
        this.schedulerService = schedulerService;
        this.authService = authService;
        this.userDAO = userDAO;
    }
    
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        Map<String, Object> loginResult = schedulerService.login(username, password);
        Map<String, Object> response = new HashMap<>(loginResult);
        
        if ((Boolean) response.get("success")) {
            // Add user information to the response
            try {
                Optional<User> userOpt = getUserByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("username", user.getUsername());
                    userInfo.put("role", user.getRole());
                    response.put("user", userInfo);
                }
            } catch (Exception e) {
                // User info is optional, continue without it
            }
            return ResponseEntity.ok(response);
        } else {
            response.put("error", response.get("message"));
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String password = userData.get("password");
        String role = userData.get("role");
        
        Optional<User> userOpt = authService.registerUser(username, password, role);
        
        Map<String, Object> response = new HashMap<>();
        if (userOpt.isPresent()) {
            response.put("success", true);
            response.put("message", "User registered successfully");
        } else {
            response.put("success", false);
            response.put("error", "Registration failed. Username may already exist.");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String token) {
        schedulerService.logout(token);
        Map<String, Object> response = Map.of("success", true, "message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@RequestHeader("Authorization") String token) {
        // Validate token first
        User user = schedulerService.validateToken(token.startsWith("Bearer ") ? token.substring(7) : token);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", getUserCount());
        stats.put("totalCourses", getCourseCount());
        stats.put("totalRooms", getRoomCount());
        stats.put("upcomingClasses", getUpcomingClassesCount());
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        List<User> users = schedulerService.getAllUsers(actualToken);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/rooms") 
    public ResponseEntity<List<Room>> getRooms(@RequestHeader("Authorization") String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        List<Room> rooms = schedulerService.getAllRooms(actualToken);
        return ResponseEntity.ok(rooms);
    }
    
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCourses(@RequestHeader("Authorization") String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        List<Course> courses = schedulerService.getAllCourses(actualToken);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/timetable")
    public ResponseEntity<List<TimetableEntry>> getTimetable(@RequestHeader("Authorization") String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        List<TimetableEntry> timetable = schedulerService.getAllBookings(actualToken);
        return ResponseEntity.ok(timetable);
    }
    
    @GetMapping("/bookings")
    public ResponseEntity<List<TimetableEntry>> getBookings(@RequestHeader("Authorization") String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        List<TimetableEntry> bookings = schedulerService.getAllBookings(actualToken);
        return ResponseEntity.ok(bookings);
    }
    
    // Helper methods
    private Optional<User> getUserByUsername(String username) {
        try {
            return userDAO.getUserByUsername(username);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private long getUserCount() {
        return 0; // Implement with actual user count
    }
    
    private long getCourseCount() {
        return 0; // Implement with actual course count  
    }
    
    private long getRoomCount() {
        return 0; // Implement with actual room count
    }
    
    private long getUpcomingClassesCount() {
        return 0; // Implement with actual upcoming classes count
    }
}