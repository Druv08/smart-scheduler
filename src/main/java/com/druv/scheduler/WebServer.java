package com.druv.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.options;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class WebServer {
    private static final Gson gson = new Gson();
    private static final int DEFAULT_PORT = 8080;
    private static SchedulerService schedulerService;

    public static void start(SchedulerService service) {
        schedulerService = service;
        if (schedulerService == null) {
            throw new IllegalArgumentException("SchedulerService cannot be null");
        }

        port(DEFAULT_PORT);
        
        // Configure static file location
        staticFiles.location("/public");  // Serve files from resources/public
        
        // Enable CORS and logging
        enableCORS();
        enableRequestLogging();
        
        // Configure routes
        configureRoutes();
        
        // Start message
        System.out.println("Web server started on http://localhost:" + DEFAULT_PORT);
    }

    private static void enableCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.type("application/json");
        });
    }

    private static void enableRequestLogging() {
        before((request, response) -> {
            System.out.println(String.format("%s %s", request.requestMethod(), request.pathInfo()));
        });
    }

    private static void configureRoutes() {
        // Root path redirects to index.html
        get("/", (req, res) -> {
            res.type("text/html");  // Set content type for redirect
            res.redirect("/index.html");
            return null;
        });

        // API routes grouped under /api path
        path("/api", () -> {
            // Set JSON content type for all API routes
            before("/*", (req, res) -> {
                res.type("application/json");
            });

            // Health check endpoint
            get("/health", (req, res) -> {
                return gson.toJson(Map.of(
                    "status", "OK",
                    "timestamp", new Date(),
                    "version", "1.0"
                ));
            });

            // Get all users
            get("/users", WebServer::getAllUsers);

            // Add new user
            post("/users", WebServer::createUser);

            // Rooms endpoints
            path("/rooms", () -> {
                get("", (req, res) -> {
                    try {
                        List<Room> rooms = schedulerService.getAllRooms();
                        return gson.toJson(Map.of(
                            "success", true,
                            "rooms", rooms,
                            "count", rooms.size()
                        ));
                    } catch (Exception e) {
                        res.status(500);
                        return gson.toJson(Map.of(
                            "success", false,
                            "error", "Failed to fetch rooms: " + e.getMessage()
                        ));
                    }
                });

                post("", (req, res) -> {
                    try {
                        Map<String, Object> body = gson.fromJson(req.body(), Map.class);
                        String roomName = (String) body.get("room_name");
                        Integer capacity = ((Number) body.get("capacity")).intValue();

                        if (roomName == null || roomName.trim().isEmpty()) {
                            throw new IllegalArgumentException("Room name is required");
                        }
                        if (capacity == null || capacity <= 0) {
                            throw new IllegalArgumentException("Valid capacity is required");
                        }

                        boolean added = schedulerService.addRoom(roomName, capacity);
                        if (added) {
                            res.status(201);
                            return gson.toJson(Map.of(
                                "success", true,
                                "message", "Room created successfully"
                            ));
                        } else {
                            res.status(409);
                            return gson.toJson(Map.of(
                                "success", false,
                                "error", "Room already exists"
                            ));
                        }
                    } catch (Exception e) {
                        res.status(400);
                        return gson.toJson(Map.of(
                            "success", false,
                            "error", e.getMessage()
                        ));
                    }
                });
            });

            // Course endpoints
            path("/courses", () -> {
                get("", (req, res) -> {
                    res.type("application/json");
                    try {
                        List<Course> courses = schedulerService.getAllCourses();
                        return gson.toJson(Map.of(
                            "success", true,
                            "courses", courses,
                            "count", courses.size()
                        ));
                    } catch (Exception e) {
                        res.status(500);
                        return gson.toJson(Map.of(
                            "success", false,
                            "error", "Failed to fetch courses: " + e.getMessage()
                        ));
                    }
                });

                post("", (req, res) -> {
                    res.type("application/json");
                    try {
                        Map<String, Object> body = gson.fromJson(req.body(), Map.class);
                        String courseCode = (String) body.get("course_code");
                        String courseName = (String) body.get("course_name");
                        String facultyUsername = (String) body.get("faculty_username");
                        Integer maxStudents = ((Number) body.get("max_students")).intValue();

                        boolean added = schedulerService.addCourse(
                            courseCode, courseName, facultyUsername, maxStudents
                        );

                        if (added) {
                            res.status(201);
                            return gson.toJson(Map.of(
                                "success", true,
                                "message", "Course created successfully"
                            ));
                        } else {
                            res.status(409);
                            return gson.toJson(Map.of(
                                "success", false,
                                "error", "Course code already exists"
                            ));
                        }
                    } catch (Exception e) {
                        res.status(400);
                        return gson.toJson(Map.of(
                            "success", false,
                            "error", e.getMessage()
                        ));
                    }
                });
            });
        });

        // Exception handling
        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body(gson.toJson(Map.of(
                "success", false,
                "error", e.getMessage(),
                "path", req.pathInfo()
            )));
        });

        // Catch-all for 404s
        notFound((req, res) -> {
            res.status(404);
            return gson.toJson(Map.of(
                "success", false,
                "error", "Route not found",
                "path", req.pathInfo()
            ));
        });

        // Shutdown endpoint
        get("/shutdown", (req, res) -> {
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    stop();
                    System.exit(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            return "Server shutting down...";
        });
    }

    private static Object getAllUsers(spark.Request req, spark.Response res) {
        try {
            // Get pagination parameters from query
            int page = parseIntParam(req.queryParams("page"), 1);
            int limit = parseIntParam(req.queryParams("limit"), 10);
            
            // Get users from service
            List<User> users = schedulerService.getAllUsers();
            
            // Apply pagination
            int start = (page - 1) * limit;
            int end = Math.min(start + limit, users.size());
            List<User> paginatedUsers = users.subList(
                Math.min(start, users.size()), 
                Math.min(end, users.size())
            );

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", paginatedUsers);
            response.put("total", users.size());
            response.put("page", page);
            response.put("limit", limit);
            response.put("pages", (int) Math.ceil(users.size() / (double) limit));

            res.status(200);
            return gson.toJson(response);

        } catch (IllegalArgumentException e) {
            res.status(400);
            return gson.toJson(Map.of(
                "success", false,
                "error", "Invalid parameters: " + e.getMessage()
            ));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of(
                "success", false,
                "error", "Failed to fetch users: " + e.getMessage()
            ));
        }
    }

    private static Object createUser(spark.Request req, spark.Response res) {
        try {
            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            validateUserInput(body);
            
            String username = body.get("username");
            String password = body.get("password");
            String role = body.get("role");

            boolean added = schedulerService.addUser(username, password, role);
            if (added) {
                res.status(201);
                return gson.toJson(Map.of(
                    "success", true,
                    "message", "User created successfully",
                    "username", username
                ));
            } else {
                res.status(409); // Conflict for duplicate username
                return gson.toJson(Map.of(
                    "success", false,
                    "error", "Username already exists"
                ));
            }
        } catch (IllegalArgumentException e) {
            res.status(400);
            return gson.toJson(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of(
                "success", false,
                "error", "Failed to create user: " + e.getMessage()
            ));
        }
    }

    private static void validateUserInput(Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role");

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (role == null || !isValidRole(role)) {
            throw new IllegalArgumentException("Invalid role. Must be student, faculty, or admin");
        }
    }

    private static boolean isValidRole(String role) {
        return role != null && 
               (role.equals("student") || 
                role.equals("faculty") || 
                role.equals("admin"));
    }

    // Helper method for parsing pagination parameters
    private static int parseIntParam(String value, int defaultValue) {
        try {
            return value != null ? Math.max(1, Integer.parseInt(value)) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void stop() {
        System.out.println("Stopping web server...");
        spark.Spark.stop();
    }
}
