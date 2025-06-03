package com.microservices.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class KeycloakInitializationService implements CommandLineRunner {

    @Autowired
    private KeycloakService keycloakService;
    
    @Value("${keycloak.auth-server-url:http://keycloak:8080}")
    private String keycloakUrl;

    @Override
    public void run(String... args) throws Exception {
        // Wait for Keycloak to be ready
        int maxRetries = 30;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                System.out.println("Checking Keycloak connection... attempt " + (retryCount + 1));
                keycloakService.getAdminToken();
                System.out.println("✅ Connected to Keycloak successfully");
                break;
            } catch (Exception e) {
                System.err.println("❌ Keycloak connection failed: " + e.getMessage());
                retryCount++;
                if (retryCount >= maxRetries) {
                    System.err.println("❌ Failed to connect to Keycloak after " + maxRetries + " attempts");
                    System.err.println("❌ Users should be imported via realm JSON file");
                    return;
                }
                Thread.sleep(10000); // Wait 10 seconds
            }
        }
        
        try {
            boolean adminExists = keycloakService.userExistsInKeycloak("admin@admin.com");
            boolean testExists = keycloakService.userExistsInKeycloak("test@test.com");
            
            System.out.println("Keycloak User Status:");
            System.out.println("   Admin user (admin@admin.com): " + (adminExists ? "EXISTS" : "MISSING"));
            System.out.println("   Test user (test@test.com): " + (testExists ? "EXISTS" :  "MISSING"));
            
            if (!adminExists || !testExists) {
                System.err.println("Users are missing from Keycloak realm import!");
                System.err.println("Check if keycloak-realm.json is properly mounted and imported");
            }
            
        } catch (Exception e) {
            System.err.println("Error checking Keycloak users: " + e.getMessage());
        }
    }
}
