package com.microservices.note.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class User {
    
    @Id
    private String userId; 
    
    private String email;
    private String username;
    
    public User() {}
    
    public User(String userId, String email, String username) {
        this.userId = userId;
        this.email = email;
        this.username = username;
    }
    
    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
