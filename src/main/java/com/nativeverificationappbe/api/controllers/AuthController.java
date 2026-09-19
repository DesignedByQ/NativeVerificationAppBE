package com.nativeverificationappbe.api.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("nativeverificationappbe/api/auth")
public class AuthController {

    /**
     * Login endpoint to create a fresh secure session
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        // 1. Authenticate user against database & hash comparison
        User user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        // 2. Create or fetch active HTTP session
        HttpSession session = request.getSession(true);

        // 3. Dynamically set session attributes from DB record
        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("ROLES", user.getRoles()); // e.g., ["ROLE_USER"]

        return ResponseEntity.ok(Map.of("message", "Logged in successfully"));
    }

    /**
     * Session Refresh / Rotation Endpoint
     * Invalidates the current session ID and issues a new secure session ID to the client cookie.
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No active session found. Please log in again."));
        }

        // Servlet 3.1+ built-in function to rotate the Session ID
        // Migrates attributes to the new session ID and deletes the old key in Redis
        String newSessionId = request.changeSessionId();

        return ResponseEntity.ok(Map.of(
                "message", "Session rotated successfully",
                "status", "ACTIVE"
        ));
    }

    /**
     * Logout and destroy the session from Redis
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Deletes session from Redis store directly
        }
        return ResponseEntity.noContent().build();
    }
}
