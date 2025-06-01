package com.microservices.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InitializationService implements CommandLineRunner {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private KeycloakService keycloakService;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if admin user already exists
            if (!userServiceClient.userExistsByEmail("admin@admin.com")) {
                // Create admin user in user-service
                userServiceClient.createUser(
                    "Admin",
                    "User", 
                    "admin@admin.com",
                    LocalDate.of(1990, 1, 1).toString(),
                    "ADMIN"
                );
                
                // Create admin user in Keycloak
                keycloakService.createUser(
                    "admin",
                    "admin@admin.com", 
                    "zaq1@WSX",
                    "Admin",
                    "User"
                );
                
                System.out.println("✅ Admin user created successfully:");
                System.out.println("   Email: admin@admin.com");
                System.out.println("   Username: admin");
                System.out.println("   Password: zaq1@WSX");
            } else {
                System.out.println("ℹ️ Admin user already exists");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to create admin user: " + e.getMessage());
            // Don't throw exception to avoid startup failure
        }
    }
}
