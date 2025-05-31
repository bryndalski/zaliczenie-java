package com.microservices.auth.service;

import com.microservices.auth.dto.LoginRequest;
import com.microservices.auth.dto.RefreshTokenRequest;
import com.microservices.auth.dto.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", loginRequest.getUsername());
        map.add("password", loginRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
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

    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", authentication.getName());
            userInfo.put("authorities", authentication.getAuthorities());
            return ResponseEntity.ok(userInfo);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Add simple health check method
    public ResponseEntity<?> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "auth-service");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(health);
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
}
