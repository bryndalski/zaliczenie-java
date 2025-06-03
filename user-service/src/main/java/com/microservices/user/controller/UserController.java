package com.microservices.user.controller;

import com.microservices.user.dto.CreateUserRequest;
import com.microservices.user.model.User;
import com.microservices.user.service.UserService;
import com.microservices.user.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management endpoints (JWT protected)")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user with auto-generated ID and timestamps")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User createdUser = userService.createUser(request);
        return ResponseEntity.ok(createdUser);
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Get the authenticated user's profile")
    @ApiResponse(responseCode = "200", description = "User profile retrieved")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String userId = extractUserIdFromJwt(jwt);
        return userService.findUserById(userId)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user by their ID (admin only)")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUserById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {
        
        String currentUserId = extractUserIdFromJwt(jwt);
        
        if (!currentUserId.equals(id) && !hasAdminRole(jwt)) {
            throw new RuntimeException("Access denied: You can only access your own profile");
        }
        
        return userService.findUserById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users (admin only)")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<List<User>> getAllUsers(@AuthenticationPrincipal Jwt jwt) {
        if (!hasAdminRole(jwt)) {
            throw new RuntimeException("Access denied: Admin role required");
        }
        
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Update the authenticated user's profile")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> updateCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody User user) {
        
        String userId = extractUserIdFromJwt(jwt);
        User updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information (admin only)")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> updateUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id, 
            @RequestBody User user) {
        
        if (!hasAdminRole(jwt)) {
            throw new RuntimeException("Access denied: Admin role required");
        }
        
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete user by ID (admin only)")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {
        
        if (!hasAdminRole(jwt)) {
            throw new RuntimeException("Access denied: Admin role required");
        }
        
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user by their email (admin only)")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUserByEmail(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String email) {
        
        if (!hasAdminRole(jwt)) {
            throw new RuntimeException("Access denied: Admin role required");
        }
        
        return userService.findUserByEmail(email)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
    
    private String extractUserIdFromJwt(Jwt jwt) {
        // Try multiple possible claims for user ID
        String userId = jwt.getClaimAsString("user_id");
        if (userId == null) {
            userId = jwt.getClaimAsString("sub");
        }
        if (userId == null) {
            userId = jwt.getClaimAsString("preferred_username");
        }
        if (userId == null) {
            throw new RuntimeException("No user ID found in JWT token");
        }
        return userId;
    }
    
    private boolean hasAdminRole(Jwt jwt) {
        // Check if user has admin role in JWT token
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map) {
            Object roles = ((Map<?, ?>) realmAccess).get("roles");
            if (roles instanceof List) {
                return ((List<?>) roles).contains("admin");
            }
        }
        return false;
    }
}
