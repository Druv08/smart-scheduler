package com.druv.scheduler;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SchedulerService {
    private final UserDAO userDAO;
    private final CourseDAO courseDAO;
    private final RoomDAO roomDAO;
    private final TimetableDAO timetableDAO;
    private final Map<String, SessionInfo> activeSessions = new HashMap<>();

    private static class SessionInfo {
        User user;
        long timestamp;

        SessionInfo(User user) {
            this.user = user;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 24 * 60 * 60 * 1000;
        }
    }

    public SchedulerService(UserDAO userDAO, CourseDAO courseDAO, RoomDAO roomDAO, TimetableDAO timetableDAO) {
        this.userDAO = userDAO;
        this.courseDAO = courseDAO;
        this.roomDAO = roomDAO;
        this.timetableDAO = timetableDAO;
    }

    // Authentication methods
    public User validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        String actualToken = token.substring(7);
        SessionInfo session = activeSessions.get(actualToken);
        
        if (session == null || session.isExpired()) {
            activeSessions.remove(actualToken);
            return null;
        }

        return session.user;
    }

    public Map<String, Object> login(String username, String password) {
        if (username == null || password == null || 
            username.trim().isEmpty() || password.trim().isEmpty()) {
            return Map.of("success", false, "message", "Invalid credentials");
        }

        try {
            Optional<User> userOpt = userDAO.getUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (HashUtil.verifyPassword(password, user.getPassword())) {
                    String token = generateToken();
                    activeSessions.put(token, new SessionInfo(user));
                    return Map.of("success", true, "token", token);
                }
            }
        } catch (Exception e) {
            return Map.of("success", false, "message", "Authentication error");
        }

        return Map.of("success", false, "message", "Invalid credentials");
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String actualToken = token.substring(7);
            activeSessions.remove(actualToken);
        }
    }

    // Protected methods that require authentication
    private boolean isAuthorized(String token, String requiredRole) {
        User user = validateToken(token);
        return user != null && user.getRole().equals(requiredRole);
    }

    // Course methods
    public List<Course> getAllCourses(String token) {
        if (validateToken(token) != null) {
            return courseDAO.findAll();
        }
        return List.of();
    }

    public boolean addCourse(String token, String code, String name, String faculty, int maxStudents) {
        if (!isAuthorized(token, "ADMIN")) {
            return false;
        }
        return courseDAO.addCourse(code, name, faculty, maxStudents);
    }

    // Room methods
    public List<Room> getAllRooms(String token) {
        if (validateToken(token) != null) {
            return roomDAO.findAll();
        }
        return List.of();
    }

    public boolean addRoom(String token, String name, int capacity) {
        if (!isAuthorized(token, "ADMIN")) {
            return false;
        }
        return roomDAO.addRoom(name, capacity);
    }

    // Timetable methods
    public List<TimetableEntry> getAllBookings(String token) {
        if (validateToken(token) != null) {
            return timetableDAO.findAll();
        }
        return List.of();
    }

    public boolean addBooking(String token, int courseId, int roomId, 
                            String day, String startTime, String endTime) {
        User user = validateToken(token);
        if (user == null || (!user.getRole().equals("ADMIN") && !user.getRole().equals("FACULTY"))) {
            return false;
        }
        
        if (timetableDAO.hasTimeConflict(roomId, day, startTime, endTime)) {
            return false;
        }
        
        return timetableDAO.addBooking(courseId, roomId, day, startTime, endTime);
    }

    // User methods with auth
    public List<User> getAllUsers(String token) {
        if (isAuthorized(token, "ADMIN")) {
            return userDAO.getAllUsers();
        }
        return List.of();
    }

    private boolean isValidToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        
        String actualToken = token.substring(7);
        SessionInfo session = activeSessions.get(actualToken);
        
        if (session == null || session.isExpired()) {
            activeSessions.remove(actualToken);
            return false;
        }
        
        return true;
    }

    public boolean addUser(String username, String password, String role) {
        try {
            String hashedPassword = HashUtil.hashPassword(password);
            User newUser = userDAO.addUser(username, hashedPassword, role);
            return newUser != null;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
