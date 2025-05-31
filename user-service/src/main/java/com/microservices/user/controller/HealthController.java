package com.microservices.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the user service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<?> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "user-service");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/api-docs")
    @Operation(summary = "API Documentation", description = "Get API documentation for user service")
    public ResponseEntity<?> apiDocs() {
        Map<String, Object> apiDoc = new HashMap<>();
        apiDoc.put("service", "user-service");
        apiDoc.put("version", "1.0.0");
        apiDoc.put("description", "User management service");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("POST /register", "Register new user");
        endpoints.put("GET /{id}", "Get user by ID");
        endpoints.put("PUT /{id}", "Update user");
        endpoints.put("DELETE /{id}", "Delete user");
        endpoints.put("GET /health", "Health check");
        
        apiDoc.put("endpoints", endpoints);
        apiDoc.put("baseUrl", "/users");
        
        return ResponseEntity.ok(apiDoc);
    }
}
