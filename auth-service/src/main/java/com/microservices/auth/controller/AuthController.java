package com.microservices.auth.controller;

import com.microservices.auth.dto.LoginRequest;
import com.microservices.auth.dto.RefreshTokenRequest;
import com.microservices.auth.dto.RegisterRequest;
import com.microservices.auth.dto.ResetPasswordRequest;
import com.microservices.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    public ResponseEntity<?> logout(
            @Parameter(description = "JWT token", required = true)
            @RequestHeader("Authorization") String token) {
        return authService.logout(token);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Get new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest refreshRequest) {
        return authService.refreshToken(refreshRequest);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Initiate password reset process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset initiated"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetRequest) {
        return authService.resetPassword(resetRequest);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getCurrentUser(@RequestParam(required = false) String username) {
        if (username == null || username.isEmpty()) {
            username = "guest";
        }
        return authService.getCurrentUser(username);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    public ResponseEntity<?> health() {
        return authService.healthCheck();
    }

    @GetMapping("/api-docs")
    @Operation(summary = "API Documentation", description = "Get API documentation for auth service")
    public ResponseEntity<?> apiDocs() {
        return authService.getApiDocumentation();
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user in both user-service and Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
}
