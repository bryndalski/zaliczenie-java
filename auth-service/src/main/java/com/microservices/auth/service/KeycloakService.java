package com.microservices.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class KeycloakService {
    
    private final RestTemplate restTemplate;
    private final String keycloakUrl;
    private final String realm;
    private final String adminUsername;
    private final String adminPassword;

    public KeycloakService(
            RestTemplate restTemplate,
            @Value("${keycloak.auth-server-url:http://keycloak:8080}") String keycloakUrl,
            @Value("${keycloak.realm:microservices}") String realm,
            @Value("${keycloak.admin.username:admin}") String adminUsername,
            @Value("${keycloak.admin.password:admin}") String adminPassword
    ) {
        this.restTemplate = restTemplate;
        this.keycloakUrl = keycloakUrl;
        this.realm = realm;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }
    
    public String getAdminUsername() {
        return adminUsername;
    }
    
    public String getAdminToken() {
        String tokenUrl = keycloakUrl + "/realms/master/protocol/openid-connect/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", "admin-cli");
        map.add("username", adminUsername);
        map.add("password", adminPassword);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        
        try {
            System.out.println("🔐 Testing Keycloak admin connection...");
            System.out.println("URL: " + tokenUrl);
            System.out.println("Username: " + adminUsername);
            System.out.println("Password length: " + (adminPassword != null ? adminPassword.length() : "null"));
            
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> body = response.getBody();
            
            if (body != null && body.containsKey("access_token")) {
                System.out.println("✅ Keycloak admin authentication successful");
                return (String) body.get("access_token");
            } else {
                System.err.println("❌ No access token in response: " + body);
                throw new RuntimeException("No access token in response");
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("Check if Keycloak admin user exists and credentials are correct");
            throw new RuntimeException("Failed to authenticate with Keycloak admin: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get admin token: " + e.getMessage());
        }
    }
    
    public boolean createUser(String username, String email, String password, String firstName, String lastName) {
        String adminToken = getAdminToken();
        String usersUrl = keycloakUrl + "/admin/realms/" + realm + "/users";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        
        Map<String, Object> userRep = new HashMap<>();
        userRep.put("username", username);
        userRep.put("email", email);
        userRep.put("firstName", firstName);
        userRep.put("lastName", lastName);
        userRep.put("enabled", true);
        userRep.put("emailVerified", true);
        
        // Set password
        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", false);
        userRep.put("credentials", List.of(credential));
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userRep, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(usersUrl, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage());
        }
    }
    
    public boolean userExistsInKeycloak(String emailOrUsername) {
        try {
            String adminToken = getAdminToken();
            
            // Try by email first
            String usersUrl = keycloakUrl + "/admin/realms/" + realm + "/users?email=" + emailOrUsername;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<List> response = restTemplate.exchange(usersUrl, HttpMethod.GET, request, List.class);
            List<Map<String, Object>> users = response.getBody();
            
            if (users != null && !users.isEmpty()) {
                return true;
            }
            
            // If not found by email, try by username
            usersUrl = keycloakUrl + "/admin/realms/" + realm + "/users?username=" + emailOrUsername;
            response = restTemplate.exchange(usersUrl, HttpMethod.GET, request, List.class);
            users = response.getBody();
            
            return users != null && !users.isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }
}
