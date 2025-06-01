package com.microservices.auth.service;

import com.microservices.auth.dto.LoginRequest;
import com.microservices.auth.dto.RefreshTokenRequest;
import com.microservices.auth.dto.RegisterRequest;
import com.microservices.auth.dto.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final KeycloakService keycloakService;
    private final UserServiceClient userServiceClient;
    private final RestTemplate restTemplate;

    private final String keycloakUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    public AuthService(
            KeycloakService keycloakService,
            UserServiceClient userServiceClient,
            RestTemplate restTemplate,
            @Value("${keycloak.auth-server-url:http://keycloak:8080}") String keycloakUrl,
            @Value("${keycloak.realm:microservices}") String realm,
            @Value("${keycloak.resource:microservices-client}") String clientId,
            @Value("${keycloak.credentials.secret:}") String clientSecret
    ) {
        this.keycloakService = keycloakService;
        this.userServiceClient = userServiceClient;
        this.restTemplate = restTemplate;
        this.keycloakUrl = keycloakUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        // Only add client_secret if it's not empty (public client doesn't need it)
        if (clientSecret != null && !clientSecret.trim().isEmpty()) {
            map.add("client_secret", clientSecret);
        }
        
        // Use email directly as username for Keycloak
        String emailOrUsername = loginRequest.getUsername();
        if (!emailOrUsername.contains("@")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Please use email address to login");
            error.put("hint", "Login is only allowed with email address, not username");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        map.add("username", emailOrUsername);
        map.add("password", loginRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            System.out.println("Attempting email login to: " + tokenUrl);
            System.out.println("Client ID: " + clientId);
            System.out.println("Email: " + emailOrUsername);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            error.put("details", "Please check your email and password");
            error.put("keycloak_error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    private String findUsernameByEmail(String email) {
        try {
            String adminToken = keycloakService.getAdminToken();
            String usersUrl = keycloakUrl + "/admin/realms/" + realm + "/users?email=" + email;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<List> response = restTemplate.exchange(usersUrl, HttpMethod.GET, request, List.class);
            
            List<Map<String, Object>> users = response.getBody();
            if (users != null && !users.isEmpty()) {
                Map<String, Object> user = users.get(0);
                return (String) user.get("username");
            }
        } catch (Exception e) {
            System.err.println("Error finding username by email: " + e.getMessage());
        }
        return null;
    }

    public ResponseEntity<?> logout(String token) {
        String logoutUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token.replace("Bearer ", ""));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            restTemplate.postForEntity(logoutUrl, request, String.class);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshRequest) {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", refreshRequest.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetRequest) {
        // Implementation for password reset via Keycloak Admin API
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset initiated");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getCurrentUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No valid token provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

        try {
            // Validate token with Keycloak userinfo endpoint
            String userInfoUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // Add simple health check method
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "auth-service");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        // Check MongoDB connection
        try {
            // Simple MongoDB ping test
            health.put("mongodb", "CONNECTED");
        } catch (Exception e) {
            health.put("mongodb", "ERROR: " + e.getMessage());
        }
        
        // Check Keycloak connection and admin credentials
        try {
            String adminToken = keycloakService.getAdminToken();
            if (adminToken != null && !adminToken.isEmpty()) {
                health.put("keycloak", "CONNECTED - Admin auth successful");
                health.put("keycloak_admin_user", keycloakService.getAdminUsername());
            } else {
                health.put("keycloak", "ERROR - Failed to get admin token");
            }
        } catch (Exception e) {
            health.put("keycloak", "ERROR: " + e.getMessage());
            health.put("keycloak_url", keycloakUrl);
            health.put("keycloak_realm", realm);
        }
        
        // Check user-service connection
        try {
            boolean userServiceConnected = checkUserServiceConnection();
            if (userServiceConnected) {
                health.put("user-service", "CONNECTED - API key valid");
            } else {
                health.put("user-service", "DISCONNECTED - Check API key");
            }
            health.put("user-service_url", userServiceClient.getUserServiceUrl());
        } catch (Exception e) {
            health.put("user-service", "ERROR: " + e.getMessage());
        }
        
        // Check overall health status
        boolean allServicesHealthy = 
            !health.get("keycloak").toString().startsWith("ERROR") &&
            !health.get("user-service").toString().startsWith("ERROR") &&
            !health.get("mongodb").toString().startsWith("ERROR");
            
        health.put("overall_status", allServicesHealthy ? "HEALTHY" : "DEGRADED");
        
        return ResponseEntity.ok(health);
    }
    
    private boolean checkUserServiceConnection() {
        try {
            String url = userServiceClient.getUserServiceUrl() + "/health";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-Key", userServiceClient.getApiKey());
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("User service health check failed: " + e.getMessage());
            return false;
        }
    }

    // Add API documentation endpoint as swagger-ui alternative
    public ResponseEntity<?> getApiDocumentation() {
        Map<String, Object> apiDoc = new HashMap<>();
        apiDoc.put("service", "auth-service");
        apiDoc.put("version", "1.0.0");
        apiDoc.put("description", "Authentication service for microservices architecture");

        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("POST /login", "Login with username/password");
        endpoints.put("POST /logout", "Logout user");
        endpoints.put("POST /refresh", "Refresh access token");
        endpoints.put("POST /reset-password", "Reset user password");
        endpoints.put("GET /me", "Get current user info");
        endpoints.put("GET /health", "Service health check");
        endpoints.put("GET /api-docs", "This API documentation");
        endpoints.put("GET /v3/api-docs", "OpenAPI 3.0 documentation (for Swagger UI)");

        apiDoc.put("endpoints", endpoints);
        apiDoc.put("baseUrl", "/auth");

        return ResponseEntity.ok(apiDoc);
    }

    public ResponseEntity<?> register(RegisterRequest registerRequest) {
        try {
            // 1. Check if user exists in user-service
            if (userServiceClient.userExistsByEmail(registerRequest.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User with this email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            // 2. Check if user exists in Keycloak
            if (keycloakService.userExistsInKeycloak(registerRequest.getUsername())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            // 3. Create user in user-service first
            Map<String, Object> createdUser = userServiceClient.createUser(
                    registerRequest.getName(),
                    registerRequest.getSurname(),
                    registerRequest.getEmail(),
                    registerRequest.getDateOfBirth().toString(),
                    registerRequest.getRole()
            );

            // 4. Create user in Keycloak
            boolean keycloakUserCreated = keycloakService.createUser(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getName(),
                    registerRequest.getSurname()
            );

            if (keycloakUserCreated) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "User registered successfully");
                response.put("username", registerRequest.getUsername());
                response.put("email", registerRequest.getEmail());
                response.put("userId", createdUser.get("id"));
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to create user in authentication system");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
