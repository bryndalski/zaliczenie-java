package com.example.auth.users.controllers;

import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
//    private final UserService userService;

//    public UserController(UserService userService) {
//        this.userService = userService;
//    }

   @GetMapping
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Users fetched successfully");
        return response;
    }
//
//    @GetMapping("/{id}")
//    public User getUserById(@PathVariable Long id) {
//        return userService.getUserById(id);
//    }
//
//    @PostMapping
//    public User createUser(@RequestBody User user) {
//        return userService.createUser(user);
//    }
//
//    @PutMapping("/{id}")
//    public User updateUser(@PathVariable Long id, @RequestBody User user) {
//        return userService.updateUser(id, user);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//    }
}
