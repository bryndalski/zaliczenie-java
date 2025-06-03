package com.microservices.user.service;

import com.microservices.user.dto.CreateUserRequest;
import com.microservices.user.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InitializationService implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if admin user already exists
            if (!userService.userExistsByEmail("admin@admin.com")) {
                CreateUserRequest adminRequest = new CreateUserRequest(
                    "Admin",
                    "User",
                    "admin@admin.com",
                    LocalDate.of(1990, 1, 1),
                    UserRole.ADMIN
                );
                
                userService.createUser(adminRequest);
                System.out.println("✅ Admin user profile created in user-service");
            } else {
                System.out.println("ℹ️ Admin user profile already exists in user-service");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to create admin user in user-service: " + e.getMessage());
        }
    }
}
