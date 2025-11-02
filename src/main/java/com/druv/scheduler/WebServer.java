package com.druv.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@Controller
@RestController
public class WebServer {

    private final CourseDAO courseDAO;
    private final RoomDAO roomDAO;
    private final UserDAOImpl userDAO;
    private final TimetableDAO timetableDAO;
    private final AuthService authService;
    private final Security security;
    private final com.druv.scheduler.service.ReportService reportService;

    @Autowired
    public WebServer(CourseDAO courseDAO, RoomDAO roomDAO, UserDAOImpl userDAO, TimetableDAO timetableDAO, 
                     AuthService authService, Security security, com.druv.scheduler.service.ReportService reportService) {
        this.courseDAO = courseDAO;
        this.roomDAO = roomDAO;
        this.userDAO = userDAO;
        this.timetableDAO = timetableDAO;
        this.authService = authService;
        this.security = security;
        this.reportService = reportService;
    }

    // ==================== AUTHENTICATION API ====================
    
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            System.out.println("Login attempt - Username: " + username + ", Password provided: " + (password != null && !password.isEmpty()));
            
            Optional<String> sessionToken = authService.login(username, password);
            
            if (sessionToken.isPresent()) {
                // Get user info for response
                Optional<User> userOpt = userDAO.getUserByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // Store session token in HTTP session
                    session.setAttribute("sessionToken", sessionToken.get());
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("role", user.getRole());
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("role", user.getRole());
                    response.put("username", user.getUsername());
                    response.put("message", "Login successful");
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            
        } catch (AuthenticationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
        try {
            String username = userData.get("username");
            String password = userData.get("password");
            String role = userData.get("role");
            
            Optional<User> newUser = authService.registerUser(username, password, role);
            
            if (newUser.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "User registered successfully");
                response.put("username", newUser.get().getUsername());
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Registration failed - username may already exist");
            return ResponseEntity.badRequest().body(response);
            
        } catch (AuthenticationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken != null) {
            security.invalidateSession(sessionToken);
        }
        session.invalidate();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/session")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSession(HttpSession session) {
        String sessionToken = (String) session.getAttribute("sessionToken");
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        
        if (sessionToken != null && security.getSession(sessionToken) != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", true);
            response.put("userId", userId);
            response.put("role", role);
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ==================== ROLE-BASED ACCESS CONTROL ====================
    
    @GetMapping("/api/admin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> adminPage(HttpSession session) {
        if (!isAuthorized(session, "admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Admin access required"));
        }
        return ResponseEntity.ok(Map.of("message", "Welcome to admin panel", "role", "admin"));
    }

    @GetMapping("/api/professor")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> professorPage(HttpSession session) {
        if (!isAuthorized(session, "professor", "admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Professor or Admin access required"));
        }
        return ResponseEntity.ok(Map.of("message", "Welcome to professor panel"));
    }

<<<<<<< Updated upstream
    @GetMapping("/api/student")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> studentPage(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication required"));
        }
        return ResponseEntity.ok(Map.of("message", "Welcome to student panel"));
    }

    // Helper methods for authorization
    private boolean isAuthenticated(HttpSession session) {
        String sessionToken = (String) session.getAttribute("sessionToken");
        return sessionToken != null && security.getSession(sessionToken) != null;
    }

    private boolean isAuthorized(HttpSession session, String... allowedRoles) {
        if (!isAuthenticated(session)) {
            return false;
        }
        
        String userRole = (String) session.getAttribute("role");
        if (userRole == null) {
            return false;
        }
        
        for (String role : allowedRoles) {
            if (role.equalsIgnoreCase(userRole)) {
                return true;
            }
        }
        return false;
=======
    @GetMapping("/profile-settings")
    public String profileSettings() {
        return "forward:/profile-settings.html";
    }

    @GetMapping("/bookings")
    public String bookings() {
        return "forward:/bookings.html";
    }

    @GetMapping("/scheduler-engine")
    public String schedulerEngine() {
        return "forward:/scheduler-engine.html";
>>>>>>> Stashed changes
    }

    // ==================== DEBUG API (TEMPORARY) ====================
    
    @GetMapping("/api/debug/users")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> debugUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("totalUsers", users.size());
            response.put("users", users.stream().map(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("role", user.getRole());
                userInfo.put("passwordStartsWith", user.getPassword().substring(0, Math.min(10, user.getPassword().length())));
                return userInfo;
            }).toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/debug/login-test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> debugLogin(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            Map<String, Object> response = new HashMap<>();
            response.put("receivedUsername", username);
            response.put("receivedPasswordLength", password != null ? password.length() : 0);
            
            Optional<User> userOpt = userDAO.getUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.put("userFound", true);
                response.put("storedRole", user.getRole());
                response.put("passwordCheck", security.checkPassword(password, user.getPassword()));
            } else {
                response.put("userFound", false);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== COURSES API ====================
    
    @GetMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCourses() {
        try {
            List<Course> courses = courseDAO.findAll();
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Map<String, Object> payload) {
        try {
            String courseCode = (String) payload.get("courseCode");
            String courseName = (String) payload.get("courseName");
            String faculty = (String) payload.get("faculty");
            Integer maxStudents = (Integer) payload.get("maxStudents");

            // Validation
            if (courseCode == null || courseCode.trim().isEmpty() ||
                courseName == null || courseName.trim().isEmpty() ||
                faculty == null || faculty.trim().isEmpty() ||
                maxStudents == null || maxStudents <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid input data"));
            }

            boolean success = courseDAO.addCourse(courseCode.trim(), courseName.trim(), faculty.trim(), maxStudents);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Course added successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Course code already exists"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error adding course: " + e.getMessage()));
        }
    }

    @PutMapping("/api/courses/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCourse(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        try {
            // For now, return not implemented
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("success", false, "message", "Update not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error updating course"));
        }
    }

    @DeleteMapping("/api/courses/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable int id) {
        try {
            boolean success = courseDAO.deleteCourse(id);
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Course deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Course not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error deleting course"));
        }
    }

    // ==================== ROOMS API ====================
    
    @GetMapping("/api/rooms")
    @ResponseBody
    public ResponseEntity<List<Room>> getAllRooms() {
        try {
            List<Room> rooms = roomDAO.findAll();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/rooms")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Map<String, Object> payload) {
        try {
            String roomName = (String) payload.get("roomName");
            Integer capacity = (Integer) payload.get("capacity");

            // Validation
            if (roomName == null || roomName.trim().isEmpty() ||
                capacity == null || capacity <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid input data"));
            }

            boolean success = roomDAO.addRoom(roomName.trim(), capacity);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Room added successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Room name already exists"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error adding room: " + e.getMessage()));
        }
    }

    @PutMapping("/api/rooms/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRoom(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("success", false, "message", "Update not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error updating room"));
        }
    }

    @DeleteMapping("/api/rooms/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteRoom(@PathVariable int id) {
        try {
            boolean success = roomDAO.deleteRoom(id);
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Room deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Room not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error deleting room"));
        }
    }

    // ==================== USERS API ====================
    
    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            String password = (String) payload.get("password");
            String role = (String) payload.get("role");

            // Validation
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid input data"));
            }

            // Hash the password before storing
            String hashedPassword = HashUtil.hashPassword(password);
            User newUser = userDAO.addUser(username.trim(), hashedPassword, role.trim());
            
            if (newUser != null) {
                return ResponseEntity.ok(Map.of("success", true, "message", "User added successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Username already exists"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error adding user: " + e.getMessage()));
        }
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("success", false, "message", "Update not implemented yet"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error updating user"));
        }
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable int id) {
        try {
            boolean success = userDAO.deleteUser(id);
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "User deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error deleting user"));
        }
    }

    // ==================== TIMETABLE/BOOKINGS API ====================
    
    @GetMapping("/api/timetable")
    @ResponseBody
    public ResponseEntity<List<TimetableEntry>> getAllTimetableEntries() {
        try {
            List<TimetableEntry> entries = timetableDAO.findAll();
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/timetable")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createTimetableEntry(@RequestBody Map<String, Object> payload) {
        try {
            Integer courseId = (Integer) payload.get("courseId");
            Integer roomId = (Integer) payload.get("roomId");
            String dayOfWeek = (String) payload.get("dayOfWeek");
            String startTime = (String) payload.get("startTime");
            String endTime = (String) payload.get("endTime");

            // Validation
            if (courseId == null || roomId == null ||
                dayOfWeek == null || dayOfWeek.trim().isEmpty() ||
                startTime == null || startTime.trim().isEmpty() ||
                endTime == null || endTime.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Invalid input data"));
            }

            // Enhanced conflict detection
            ConflictResult conflictCheck = checkTimeConflict(roomId, courseId, dayOfWeek.trim(), 
                startTime.trim(), endTime.trim());
            
            if (conflictCheck.hasConflict) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", conflictCheck.message, 
                               "conflictType", conflictCheck.type));
            }

            // Create enhanced timetable entry
            TimetableEntry entry = new TimetableEntry();
            entry.setCourseId(courseId);
            entry.setRoomId(roomId);
            entry.setDay(dayOfWeek.trim());
            entry.setStartTime(startTime.trim());
            entry.setEndTime(endTime.trim());
            
            // Set instructor ID
            String instructorUsername = getInstructorUsernameForCourse(courseId);
            if (instructorUsername != null) {
                Optional<User> instructorOpt = userDAO.getUserByUsername(instructorUsername);
                if (instructorOpt.isPresent()) {
                    entry.setInstructorId(instructorOpt.get().getId());
                }
            }

            boolean success = timetableDAO.addTimetableEntry(entry);
            
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Booking added successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Failed to add booking"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error adding booking: " + e.getMessage()));
        }
    }

    @DeleteMapping("/api/timetable/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteTimetableEntry(@PathVariable int id) {
        try {
            boolean success = timetableDAO.deleteEntry(id);
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Booking deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Booking not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error deleting booking"));
        }
    }

    // ==================== ADVANCED SCHEDULING & CONFLICT DETECTION ====================
    
    @PostMapping("/api/timetable/check-conflict")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkConflict(@RequestBody Map<String, Object> payload) {
        try {
            Integer courseId = (Integer) payload.get("courseId");
            Integer roomId = (Integer) payload.get("roomId");
            String day = (String) payload.get("day");
            String startTime = (String) payload.get("startTime");
            String endTime = (String) payload.get("endTime");

            if (courseId == null || roomId == null || day == null || startTime == null || endTime == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Missing required parameters"));
            }

            ConflictResult result = checkTimeConflict(roomId, courseId, day, startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasConflict", result.hasConflict);
            response.put("conflictType", result.type);
            response.put("message", result.message);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Error checking conflicts: " + e.getMessage()));
        }
    }

    @GetMapping("/api/rooms/{roomId}/availability")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkRoomAvailability(
            @PathVariable int roomId,
            @RequestBody Map<String, String> params) {
        try {
            String day = params.get("day");
            String startTime = params.get("startTime");
            String endTime = params.get("endTime");

            if (day == null || startTime == null || endTime == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("available", false, "error", "Missing parameters"));
            }

            List<TimetableEntry> conflicts = timetableDAO.getByRoomAndDay(roomId, day);
            boolean available = true;
            String conflictDetails = "";

            for (TimetableEntry entry : conflicts) {
                if (isTimeOverlap(startTime, endTime, entry.getStartTime(), entry.getEndTime())) {
                    available = false;
                    conflictDetails = String.format("Room occupied from %s to %s", 
                        entry.getStartTime(), entry.getEndTime());
                    break;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("roomId", roomId);
            response.put("day", day);
            if (!available) {
                response.put("conflict", conflictDetails);
            }

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("available", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/api/timetable/auto-generate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> autoGenerateSchedule() {
        try {
            // Clear existing timetable first
            // timetableDAO.clearAll(); // Implement this if needed
            
            List<Course> courses = courseDAO.findAll();
            List<Room> rooms = roomDAO.findAll();
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            String[] timeSlots = {
                "08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00",
                "13:00-14:00", "14:00-15:00", "15:00-16:00", "16:00-17:00"
            };
            
            int scheduledCount = 0;
            int failedCount = 0;
            StringBuilder details = new StringBuilder();
            
            for (Course course : courses) {
                boolean courseScheduled = false;
                
                dayLoop: for (String day : days) {
                    for (String slot : timeSlots) {
                        String[] times = slot.split("-");
                        String startTime = times[0];
                        String endTime = times[1];
                        
                        for (Room room : rooms) {
                            ConflictResult conflict = checkTimeConflict(room.getId(), course.getId(), day, startTime, endTime);
                            
                            if (!conflict.hasConflict) {
                                TimetableEntry entry = new TimetableEntry();
                                entry.setCourseId(course.getId());
                                entry.setRoomId(room.getId());
                                entry.setDay(day);
                                entry.setStartTime(startTime);
                                entry.setEndTime(endTime);
                                
                                // Set instructor ID
                                String instructorUsername = course.getFacultyUsername();
                                if (instructorUsername != null) {
                                    Optional<User> instructorOpt = userDAO.getUserByUsername(instructorUsername);
                                    if (instructorOpt.isPresent()) {
                                        entry.setInstructorId(instructorOpt.get().getId());
                                    }
                                }
                                
                                timetableDAO.addTimetableEntry(entry);
                                scheduledCount++;
                                courseScheduled = true;
                                
                                details.append(String.format("Scheduled %s in %s on %s %s\n", 
                                    course.getCourseName(), room.getName(), day, slot));
                                
                                break dayLoop;
                            }
                        }
                    }
                }
                
                if (!courseScheduled) {
                    failedCount++;
                    details.append(String.format("Failed to schedule %s - no available slots\n", 
                        course.getCourseName()));
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Generated schedule: %d courses scheduled, %d failed", 
                scheduledCount, failedCount));
            response.put("scheduledCount", scheduledCount);
            response.put("failedCount", failedCount);
            response.put("details", details.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Schedule generation failed: " + e.getMessage()));
        }
    }

    // ==================== HELPER METHODS FOR CONFLICT DETECTION ====================
    
    private ConflictResult checkTimeConflict(int roomId, int courseId, String day, String startTime, String endTime) {
        try {
            // Step 1: Check room overlap
            List<TimetableEntry> roomEntries = timetableDAO.getByRoomAndDay(roomId, day);
            for (TimetableEntry entry : roomEntries) {
                if (isTimeOverlap(startTime, endTime, entry.getStartTime(), entry.getEndTime())) {
                    String courseName = getCourseNameById(entry.getCourseId());
                    return new ConflictResult(true, "Room conflict", 
                        String.format("Room is already occupied by %s from %s to %s", 
                            courseName, entry.getStartTime(), entry.getEndTime()));
                }
            }

            // Step 2: Check instructor overlap
            String instructorUsername = getInstructorUsernameForCourse(courseId);
            if (instructorUsername != null) {
                Optional<User> instructorOpt = userDAO.getUserByUsername(instructorUsername);
                if (instructorOpt.isPresent()) {
                    int instructorId = instructorOpt.get().getId();
                    List<TimetableEntry> instructorEntries = timetableDAO.getByInstructorAndDay(instructorId, day);
                    
                    for (TimetableEntry entry : instructorEntries) {
                        if (isTimeOverlap(startTime, endTime, entry.getStartTime(), entry.getEndTime())) {
                            String conflictCourseName = getCourseNameById(entry.getCourseId());
                            return new ConflictResult(true, "Instructor conflict", 
                                String.format("Instructor is already teaching %s from %s to %s", 
                                    conflictCourseName, entry.getStartTime(), entry.getEndTime()));
                        }
                    }
                }
            }

            return new ConflictResult(false, "No conflict", "Time slot is available");
            
        } catch (Exception e) {
            return new ConflictResult(true, "Error", "Unable to check conflicts: " + e.getMessage());
        }
    }

    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        try {
            int start1Minutes = timeToMinutes(start1);
            int end1Minutes = timeToMinutes(end1);
            int start2Minutes = timeToMinutes(start2);
            int end2Minutes = timeToMinutes(end2);
            
            return !(end1Minutes <= start2Minutes || start1Minutes >= end2Minutes);
        } catch (Exception e) {
            return true; // Assume conflict on error for safety
        }
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private String getInstructorUsernameForCourse(int courseId) {
        try {
            List<Course> courses = courseDAO.findAll();
            return courses.stream()
                .filter(c -> c.getId() == courseId)
                .map(Course::getFacultyUsername)
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private String getCourseNameById(int courseId) {
        try {
            List<Course> courses = courseDAO.findAll();
            return courses.stream()
                .filter(c -> c.getId() == courseId)
                .map(Course::getCourseName)
                .findFirst()
                .orElse("Unknown Course");
        } catch (Exception e) {
            return "Unknown Course";
        }
    }

    // ==================== REPORTING & ANALYTICS API ====================
    
    /**
     * Get dashboard summary with analytics data
     */
    @GetMapping("/api/reports/summary")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReportSummary(HttpSession session) {
        // Check if user is authenticated
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken == null || security.getSession(sessionToken) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Map<String, Object> summary = reportService.generateSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to generate report summary");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get timetable grid data for visual display
     */
    @GetMapping("/api/reports/timetable-grid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTimetableGrid(HttpSession session) {
        // Check if user is authenticated
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken == null || security.getSession(sessionToken) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Map<String, Object> gridData = reportService.getTimetableGridData();
            return ResponseEntity.ok(gridData);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to generate timetable grid data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Export timetable data as CSV
     */
    @GetMapping("/api/export/csv")
    public void exportCSV(HttpSession session, jakarta.servlet.http.HttpServletResponse response) 
            throws java.io.IOException {
        // Check if user is authenticated
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken == null || security.getSession(sessionToken) == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication required");
            return;
        }

        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"timetable-export.csv\"");
            
            List<Map<String, Object>> csvData = reportService.getCSVExportData();
            
            // Write CSV header
            java.io.PrintWriter writer = response.getWriter();
            if (!csvData.isEmpty()) {
                Map<String, Object> firstRow = csvData.get(0);
                writer.println(String.join(",", firstRow.keySet()));
                
                // Write data rows
                for (Map<String, Object> row : csvData) {
                    List<String> values = new java.util.ArrayList<>();
                    for (Object value : row.values()) {
                        String strValue = value != null ? value.toString() : "";
                        // Escape commas and quotes in CSV
                        if (strValue.contains(",") || strValue.contains("\"")) {
                            strValue = "\"" + strValue.replace("\"", "\"\"") + "\"";
                        }
                        values.add(strValue);
                    }
                    writer.println(String.join(",", values));
                }
            } else {
                writer.println("No data available");
            }
            
        } catch (Exception e) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to export CSV");
        }
    }

    /**
     * Get room utilization analytics
     */
    @GetMapping("/api/reports/room-utilization")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getRoomUtilization(HttpSession session) {
        // Check if user is authenticated
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken == null || security.getSession(sessionToken) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Map<String, Object>> utilization = reportService.getRoomUtilizationStats();
            return ResponseEntity.ok(utilization);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get instructor workload analytics
     */
    @GetMapping("/api/reports/instructor-workload")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getInstructorWorkload(HttpSession session) {
        // Check if user is authenticated
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken == null || security.getSession(sessionToken) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<TimetableEntry> allEntries = timetableDAO.findAll();
            List<Map<String, Object>> workload = reportService.getInstructorWorkloadStats(allEntries);
            return ResponseEntity.ok(workload);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper class for conflict results
    private static class ConflictResult {
        final boolean hasConflict;
        final String type;
        final String message;

        ConflictResult(boolean hasConflict, String type, String message) {
            this.hasConflict = hasConflict;
            this.type = type;
            this.message = message;
        }
    }
}
