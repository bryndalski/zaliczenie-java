package com.microservices.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InitializationService implements CommandLineRunner {

    @Autowired
    private UserServiceClient userServiceClient;

    @Value("${ADMIN_EMAIL:admin@admin.com}")
    private String adminEmail;

    @Value("${ADMIN_NAME:Admin}")
    private String adminName;

    @Value("${ADMIN_SURNAME:User}")
    private String adminSurname;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Only create user profile in user-service (Keycloak users come from realm import)
            if (!userServiceClient.userExistsByEmail(adminEmail)) {
                userServiceClient.createUser(
                    adminName,
                    adminSurname, 
                    adminEmail,
                    LocalDate.of(1990, 1, 1).toString(),
                    "ADMIN"
                );
                
                System.out.println("✅ Admin user profile created in user-service:");
                System.out.println("   Email: " + adminEmail);
                System.out.println("   Note: Login credentials are handled by Keycloak realm import");
            } else {
                System.out.println("ℹ️ Admin user profile already exists in user-service");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to create admin user profile: " + e.getMessage());
        }
    }
}
