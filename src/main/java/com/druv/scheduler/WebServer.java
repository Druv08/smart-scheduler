package com.druv.scheduler;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class WebServer {
    private final UserManager userManager;
    private final RoomManager roomManager;
    private final CourseManager courseManager;
    private final TimetableManager timetableManager;

    public WebServer(UserManager userManager, RoomManager roomManager, 
                    CourseManager courseManager, TimetableManager timetableManager) {
        this.userManager = userManager;
        this.roomManager = roomManager;
        this.courseManager = courseManager;
        this.timetableManager = timetableManager;
    }

    public void start() {
        port(8080);
        staticFiles.location("/public");
        staticFiles.expireTime(600);

        // Redirect root to dashboard
        get("/", (req, res) -> {
            res.redirect("/dashboard.html");
            return null;
        });

        // User Management
        get("/users", (req, res) -> {
            List<User> users = userManager.getAllUsers();
            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Users</title></head><body>");
            html.append("<a href='/dashboard.html'>Back to Dashboard</a>");
            html.append("<h2>Add User</h2>");
            html.append("<form method='post' action='/users'>");
            html.append("Username: <input name='username'><br>");
            html.append("Password: <input type='password' name='password'><br>");
            html.append("Role: <select name='role'>");
            html.append("<option>student</option><option>faculty</option><option>admin</option>");
            html.append("</select><br>");
            html.append("<input type='submit' value='Add User'></form><hr>");
            
            html.append("<h2>User List</h2>");
            html.append("<table border='1'><tr><th>Username</th><th>Role</th></tr>");
            for (User user : users) {
                html.append("<tr><td>").append(user.getUsername())
                    .append("</td><td>").append(user.getRole())
                    .append("</td></tr>");
            }
            html.append("</table></body></html>");
            return html.toString();
        });

        // Similar patterns for rooms, courses, and bookings routes
        // ...existing route handlers...

        // Error handling
        notFound((req, res) -> {
            if (req.pathInfo().startsWith("/api/")) {
                res.type("application/json");
                return "{\"error\": \"Not found\"}";
            }
            res.type("text/html");
            return "<h1>Page Not Found</h1><a href='/dashboard.html'>Back to Dashboard</a>";
        });
    }
}
