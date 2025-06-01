package com.microservices.user.service;

import com.microservices.user.dto.CreateUserRequest;
import com.microservices.user.model.User;
import com.microservices.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        
        // Create new user with auto-generated fields
        User user = new User();
        user.setId(UUID.randomUUID().toString()); // Auto-generated UUID
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now()); // Auto-generated
        user.setUpdatedAt(LocalDateTime.now()); // Auto-generated
        
        return userRepository.save(user);
    }
    
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public User updateUser(String id, User updatedUser) {
        return userRepository.findById(id)
            .map(user -> {
                if (updatedUser.getName() != null) user.setName(updatedUser.getName());
                if (updatedUser.getSurname() != null) user.setSurname(updatedUser.getSurname());
                if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
                if (updatedUser.getDateOfBirth() != null) user.setDateOfBirth(updatedUser.getDateOfBirth());
                if (updatedUser.getRole() != null) user.setRole(updatedUser.getRole());
                user.setUpdatedAt(LocalDateTime.now()); // Auto-update timestamp
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public boolean userExists(String id) {
        return userRepository.existsById(id);
    }
    
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
