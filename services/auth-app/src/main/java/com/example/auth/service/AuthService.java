package com.example.auth.service;

import com.example.auth.client.UserServiceClient;
import com.example.auth.dto.CreateUserRequest;
import com.example.auth.dto.UserDTO;
import com.example.auth.model.AuthCredentials;
import com.example.auth.repository.AuthCredentialsRepository;
import com.example.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceClient userServiceClient;
    private final AuthCredentialsRepository authCredentialsRepository;

    public String register(CreateUserRequest request) {
        // 1. Check if user exists
        if (userServiceClient.checkUserExists(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        // 2. Create user in user-service
        UserDTO createdUser = userServiceClient.createUser(new CreateUserRequest(
            request.getUsername(),
            request.getEmail()
        ));

        // 3. Store auth credentials
        AuthCredentials credentials = new AuthCredentials();
        credentials.setUserId(createdUser.getId());
        credentials.setUserEmail(createdUser.getEmail());
        credentials.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        authCredentialsRepository.save(credentials);

        // 4. Generate and return JWT
        return jwtTokenProvider.createToken(createdUser.getEmail(), Collections.emptyList());
    }

    public String login(String email, String password) {
        AuthCredentials credentials = authCredentialsRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, credentials.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtTokenProvider.createToken(credentials.getUserEmail(), Collections.emptyList());
    }
}