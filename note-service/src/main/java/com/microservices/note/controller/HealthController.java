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
        health.put("database", "Neo4j");
        health.put("authentication", "Keycloak JWT");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/api-info")
    @Operation(summary = "API Information", description = "Get service information")
    @ApiResponse(responseCode = "200", description = "Service info retrieved")
    public ResponseEntity<?> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Note Service - REST API Only");
        response.put("service", "note-service");
        response.put("version", "1.0.0");
        response.put("database", "Neo4j Graph Database");
        response.put("authentication", "Keycloak JWT");
        response.put("endpoints", Map.of(
            "health", "/health",
            "api-docs", "/v3/api-docs",
            "notes", "/notes"
        ));
        return ResponseEntity.ok(response);
    }
}
