package com.microservices.user.service;

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
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
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
                user.setUpdatedAt(LocalDateTime.now());
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
