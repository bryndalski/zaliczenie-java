package com.example.main.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String email;
}
