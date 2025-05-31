package com.example.main.service;

import com.example.main.dto.UserDTO;
import com.example.main.dto.CreateUserRequest;
import com.example.main.model.User;
import com.example.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO register(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // ...set other fields...
        user = userRepository.save(user);
        return toDto(user);
    }

    public Optional<UserDTO> getByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDto);
    }

    public Optional<UserDTO> getById(String id) {
        return userRepository.findById(id).map(this::toDto);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        // ...set other fields...
        return dto;
    }
}
