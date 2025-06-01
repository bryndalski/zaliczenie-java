package com.microservices.user.controller;

import com.microservices.user.model.User;
import com.microservices.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api-key/users")
@Tag(name = "API Key Users", description = "User management endpoints (API Key protected)")
public class ApiKeyUserController {
    
    @Autowired
    private UserService userService;
    
    @Value("${app.api.key:default-api-key}")
    private String validApiKey;
    
    private boolean isValidApiKey(String apiKey) {
        return validApiKey.equals(apiKey);
    }
    
    @PostMapping
    @Operation(summary = "Create user (API Key)", description = "Create a new user using API key")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @ApiResponse(responseCode = "401", description = "Invalid API key")
    @ApiResponse(responseCode = "400", description = "User already exists")
    public ResponseEntity<User> createUser(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody User user) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(401).build();
        }
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (API Key)", description = "Retrieve user by their ID using API key")
    public ResponseEntity<User> getUserById(
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable String id) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(401).build();
        }
        return userService.findUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Get all users (API Key)", description = "Retrieve all users using API key")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(401).build();
        }
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user (API Key)", description = "Delete user by ID using API key")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable String id) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(401).build();
        }
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if user exists (API Key)", description = "Check if user exists by ID using API key")
    public ResponseEntity<Map<String, Boolean>> userExists(
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable String id) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(401).build();
        }
        boolean exists = userService.userExists(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @GetMapping("/email/{email}/exists")
    @Operation(summary = "Check if user exists by email (API Key)", description = "Check if user exists by email using API key")
    public ResponseEntity<Map<String, Boolean>> userExistsByEmail(
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable String email) {
        if (!isValidApiKey(apiKey)) {
            return ResponseEntity.status(401).build();
        }
        boolean exists = userService.userExistsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
