package com.microservices.note.controller;

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
    @Operation(summary = "Health check", description = "Check if the note service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<?> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "note-service");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/notes")
    @Operation(summary = "Get all notes", description = "Retrieve all notes for testing")
    @ApiResponse(responseCode = "200", description = "Notes retrieved successfully")
    public ResponseEntity<?> getAllNotes() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notes endpoint - GraphQL available at /graphql");
        response.put("service", "note-service");
        response.put("endpoints", Map.of(
            "graphql", "/graphql",
            "health", "/health",
            "api-docs", "/v3/api-docs"
        ));
        return ResponseEntity.ok(response);
    }
}
