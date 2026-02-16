package com.auth.userauth.service;

import com.auth.userauth.model.AuthRequest;
import com.auth.userauth.model.AuthResponse;
import com.auth.userauth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for user authentication operations
 */
@Service
public class AuthenticationService {

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder;
    
    // In-memory user store (in production, use database)
    private final Map<String, User> userStore;

    public AuthenticationService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userStore = new HashMap<>();
        initializeDefaultUsers();
    }

    /**
     * Initialize default users for testing
     */
    private void initializeDefaultUsers() {
        // Create default admin user
        User admin = new User("admin", "admin@example.com", 
                            passwordEncoder.encode("admin123"), "ADMIN");
        admin.setId(1L);
        userStore.put("admin", admin);

        // Create default regular user
        User user = new User("user", "user@example.com", 
                           passwordEncoder.encode("user123"), "USER");
        user.setId(2L);
        userStore.put("user", user);

        System.out.println("Initialized default users: admin (admin123), user (user123)");
    }

    /**
     * Authenticate user and generate token
     */
    public AuthResponse authenticate(AuthRequest request) {
        User user = userStore.get(request.getUsername());
        
        if (user == null) {
            throw new RuntimeException("User not found: " + request.getUsername());
        }

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getUsername(), user.getRole(), 
                              jwtService.getExpirationTime());
    }

    /**
     * Register a new user
     */
    public User registerUser(String username, String email, String password, String role) {
        if (userStore.containsKey(username)) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(username, email, passwordEncoder.encode(password), role);
        user.setId((long) (userStore.size() + 1));
        userStore.put(username, user);

        return user;
    }

    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userStore.get(username));
    }

    /**
     * Validate token and get user
     */
    public Optional<User> validateTokenAndGetUser(String token) {
        try {
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                return getUserByUsername(username);
            }
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Change user password
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userStore.get(username);
        if (user == null) {
            return false;
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return true;
    }

    /**
     * Deactivate user account
     */
    public boolean deactivateUser(String username) {
        User user = userStore.get(username);
        if (user != null) {
            user.setActive(false);
            return true;
        }
        return false;
    }

    /**
     * Activate user account
     */
    public boolean activateUser(String username) {
        User user = userStore.get(username);
        if (user != null) {
            user.setActive(true);
            return true;
        }
        return false;
    }
}

