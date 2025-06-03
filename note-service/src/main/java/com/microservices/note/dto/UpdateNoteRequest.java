package com.microservices.note.dto;

import jakarta.validation.constraints.Size;

public class UpdateNoteRequest {
    
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
