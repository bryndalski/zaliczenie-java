package com.example.main.users_module.services;

import com.example.main.users_module.entities.User;
import com.example.main.users_module.inputs.UserInput;
import com.example.main.users_module.repositor.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserInput userInput) {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name(userInput.getName())
                .surname(userInput.getSurname())
                .email(userInput.getEmail())
                .birthday(userInput.getBirthday())
                .build();

        //logging user
        System.out.println("User created: " + user);

        return userRepository.create(user);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findOneById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    public User updateUser(User user) {
        return userRepository.update(user);
    }

    public void deleteUser(User user) {
        userRepository.remove(user);
    }
}
