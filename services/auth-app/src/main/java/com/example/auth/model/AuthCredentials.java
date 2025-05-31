package com.example.auth.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Document(collection = "auth_credentials")
public class AuthCredentials {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String userId;
    
    @Indexed(unique = true)
    private String userEmail;
    
    private String passwordHash;
}
