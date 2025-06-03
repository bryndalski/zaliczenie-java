package com.microservices.user.controller;

import com.microservices.user.model.User;
import com.microservices.user.service.UserService;
import com.microservices.user.dto.CreateUserRequest;
import com.microservices.user.exception.UserNotFoundException;
import com.microservices.user.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    
    @Value("${api.key:microservice-api-key-2024}")
    private String validApiKey;
    
    private boolean isValidApiKey(String apiKey) {
        return validApiKey.equals(apiKey);
    }
    
    @PostMapping
    @Operation(summary = "Create user (API Key)", description = "Create a new user using API key")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @ApiResponse(responseCode = "401", description = "Invalid API key")
    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    public ResponseEntity<?> createUser(
            @RequestHeader("X-API-Key") String apiKey,
            @Valid @RequestBody CreateUserRequest request) {
        if (!isValidApiKey(apiKey)) {
            ErrorResponse error = new ErrorResponse(401, "Unauthorized", "Invalid API key", "/api-key/users");
            return ResponseEntity.status(401).body(error);
        }
        User createdUser = userService.createUser(request);
        return ResponseEntity.ok(createdUser);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (API Key)", description = "Retrieve user by their ID using API key")
    public ResponseEntity<?> getUserById(
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable String id) {
        if (!isValidApiKey(apiKey)) {
            ErrorResponse error = new ErrorResponse(401, "Unauthorized", "Invalid API key", "/api-key/users/" + id);
            return ResponseEntity.status(401).body(error);
        }
        return userService.findUserById(id)
            .map(user -> ResponseEntity.ok((Object) user))
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
    
    @GetMapping
    @Operation(summary = "Get all users (API Key)", description = "Retrieve all users using API key")
    public ResponseEntity<?> getAllUsers(@RequestHeader("X-API-Key") String apiKey) {
        if (!isValidApiKey(apiKey)) {
            ErrorResponse error = new ErrorResponse(401, "Unauthorized", "Invalid API key", "/api-key/users");
            return ResponseEntity.status(401).body(error);
        }
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user (API Key)", description = "Delete user by ID using API key")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("X-API-Key") String apiKey,
            @PathVariable String id) {
        if (!isValidApiKey(apiKey)) {
            ErrorResponse error = new ErrorResponse(401, "Unauthorized", "Invalid API key", "/api-key/users/" + id);
            return ResponseEntity.status(401).body(error);
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
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
