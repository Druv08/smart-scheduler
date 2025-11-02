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
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null) {
            schedulerService.logout(token);
        }
        Map<String, Object> response = Map.of("success", true, "message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@RequestHeader(value = "Authorization", required = false) String token) {
        // Validate token first, use default admin if no token provided
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        User user = schedulerService.validateToken(actualToken);
        // Allow access for demo purposes if no token provided
        if (user == null && token != null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", getUserCount(actualToken));
        stats.put("totalCourses", getCourseCount(actualToken));
        stats.put("totalRooms", getRoomCount(actualToken));
        stats.put("upcomingClasses", getUpcomingClassesCount(actualToken));
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestHeader(value = "Authorization", required = false) String token) {
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        List<User> users = schedulerService.getAllUsers(actualToken);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/rooms") 
    public ResponseEntity<List<Room>> getRooms(@RequestHeader(value = "Authorization", required = false) String token) {
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        List<Room> rooms = schedulerService.getAllRooms(actualToken);
        return ResponseEntity.ok(rooms);
    }
    
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCourses(@RequestHeader(value = "Authorization", required = false) String token) {
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        List<Course> courses = schedulerService.getAllCourses(actualToken);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/timetable")
    public ResponseEntity<List<TimetableEntry>> getTimetable(@RequestHeader(value = "Authorization", required = false) String token) {
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        List<TimetableEntry> timetable = schedulerService.getAllBookings(actualToken);
        return ResponseEntity.ok(timetable);
    }
    
    @GetMapping("/bookings")
    public ResponseEntity<List<TimetableEntry>> getBookings(@RequestHeader(value = "Authorization", required = false) String token) {
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        List<TimetableEntry> bookings = schedulerService.getAllBookings(actualToken);
        return ResponseEntity.ok(bookings);
    }
    
    @PostMapping("/courses")
    public ResponseEntity<Map<String, Object>> addCourse(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> courseData) {
        
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        
        try {
            String code = (String) courseData.get("code");
            String name = (String) courseData.get("name");
            String faculty = (String) courseData.get("faculty");
            Integer maxStudents = (Integer) courseData.get("maxStudents");
            
            if (code == null || name == null || faculty == null || maxStudents == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "Missing required fields"));
            }
            
            boolean success = schedulerService.addCourse(actualToken, code, name, faculty, maxStudents);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Course added successfully"));
            } else {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "Failed to add course"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "error", "Invalid request data"));
        }
    }
    
    @PostMapping("/rooms")
    public ResponseEntity<Map<String, Object>> addRoom(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> roomData) {
        
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        
        try {
            String name = (String) roomData.get("name");
            Integer capacity = (Integer) roomData.get("capacity");
            
            if (name == null || capacity == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "Missing required fields"));
            }
            
            boolean success = schedulerService.addRoom(actualToken, name, capacity);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Room added successfully"));
            } else {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "Failed to add room"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "error", "Invalid request data"));
        }
    }
    
    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> addBooking(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> bookingData) {
        
        String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : "admin");
        
        try {
            Integer courseId = (Integer) bookingData.get("courseId");
            Integer roomId = (Integer) bookingData.get("roomId");
            String day = (String) bookingData.get("day");
            String startTime = (String) bookingData.get("startTime");
            String endTime = (String) bookingData.get("endTime");
            
            if (courseId == null || roomId == null || day == null || startTime == null || endTime == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "Missing required fields"));
            }
            
            boolean success = schedulerService.addBooking(actualToken, courseId, roomId, day, startTime, endTime);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Booking added successfully"));
            } else {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "error", "Failed to add booking - possible time conflict"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "error", "Invalid request data"));
        }
    }
    
    // Helper methods
    private Optional<User> getUserByUsername(String username) {
        try {
            return userDAO.getUserByUsername(username);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private long getUserCount(String token) {
        return schedulerService.getUserCount(token);
    }
    
    private long getCourseCount(String token) {
        return schedulerService.getCourseCount(token);
    }
    
    private long getRoomCount(String token) {
        return schedulerService.getRoomCount(token);
    }
    
    private long getUpcomingClassesCount(String token) {
        return schedulerService.getUpcomingClassesCount(token);
    }
}