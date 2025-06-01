package com.microservices.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private final String apiKey;

    public UserServiceClient(
            RestTemplate restTemplate,
            @Value("${user-service.url:http://user-service:8080}") String userServiceUrl,
            @Value("${user-service.api-key:microservice-api-key-2024}") String apiKey
    ) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
        this.apiKey = apiKey;
    }

    public boolean userExistsByEmail(String email) {
        String url = userServiceUrl + "/api-key/users/email/" + email + "/exists";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", apiKey);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            Map<String, Object> body = response.getBody();
            return body != null && (Boolean) body.get("exists");
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> createUser(String name, String surname, String email,
                                          String dateOfBirth, String role) {
        String url = userServiceUrl + "/api-key/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);

        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("name", name);
        userRequest.put("surname", surname);
        userRequest.put("email", email);
        userRequest.put("dateOfBirth", dateOfBirth);
        userRequest.put("role", role);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in user-service: " + e.getMessage());
        }
    }

    // Add getter methods for health check
    public String getUserServiceUrl() {
        return userServiceUrl;
    }

    public String getApiKey() {
        return apiKey;
    }
}
