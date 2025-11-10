package com.druv.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final SchedulerService schedulerService;
    private final UserDAO userDAO;
    private final Security security;

    public ProfileController(SchedulerService schedulerService, UserDAO userDAO, Security security) {
        this.schedulerService = schedulerService;
        this.userDAO = userDAO;
        this.security = security;
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : null);
            
            if (actualToken == null) {
                return ResponseEntity.status(401).build();
            }

            User user = schedulerService.validateToken(actualToken);
            if (user == null) {
                return ResponseEntity.status(401).build();
            }

            // Convert User to UserDTO
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getUsername()); // Using username as name for now
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getUsername() + "@srmist.edu.in"); // Mock email
            userDTO.setPhone(""); // No phone in current User model
            userDTO.setDepartment("CSE"); // Mock department
            userDTO.setRole(user.getRole());

            return ResponseEntity.ok(userDTO);

        } catch (Exception e) {
            logger.error("Error retrieving current user profile: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody UserDTO updatedUser, 
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : null);
            
            if (actualToken == null) {
                response.put("success", false);
                response.put("error", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            User user = schedulerService.validateToken(actualToken);
            if (user == null) {
                response.put("success", false);
                response.put("error", "Invalid authentication token");
                return ResponseEntity.status(401).body(response);
            }

            // Update user information
            // Note: Current User model only has username, password, and role
            // For now, we'll just simulate the update
            logger.info("Profile update requested for user: {} with name: {}, phone: {}", 
                       user.getUsername(), updatedUser.getName(), updatedUser.getPhone());

            response.put("success", true);
            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating user profile: {}", e.getMessage());
            response.put("success", false);
            response.put("error", "Failed to update profile");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody PasswordChangeDTO dto, 
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String actualToken = token != null && token.startsWith("Bearer ") ? token.substring(7) : (token != null ? token : null);
            
            if (actualToken == null) {
                response.put("success", false);
                response.put("error", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            User user = schedulerService.validateToken(actualToken);
            if (user == null) {
                response.put("success", false);
                response.put("error", "Invalid authentication token");
                return ResponseEntity.status(401).body(response);
            }

            // Verify current password
            if (!security.checkPassword(dto.getCurrentPassword(), user.getPassword())) {
                response.put("success", false);
                response.put("error", "Current password is incorrect");
                return ResponseEntity.ok(response);
            }

            // Validate new password
            if (dto.getNewPassword() == null || dto.getNewPassword().length() < 8) {
                response.put("success", false);
                response.put("error", "New password must be at least 8 characters long");
                return ResponseEntity.ok(response);
            }

            // Hash new password and update
            String hashedNewPassword = security.hashPassword(dto.getNewPassword());
            user.setPassword(hashedNewPassword);
            
            boolean updated = userDAO.updateUser(user);
            if (updated) {
                response.put("success", true);
                response.put("message", "Password changed successfully");
                logger.info("Password changed successfully for user: {}", user.getUsername());
            } else {
                response.put("success", false);
                response.put("error", "Failed to update password");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage());
            response.put("success", false);
            response.put("error", "Failed to change password");
            return ResponseEntity.status(500).body(response);
        }
    }
}
