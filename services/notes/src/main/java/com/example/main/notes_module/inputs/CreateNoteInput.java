package com.example.main.notes_module.inputs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateNoteInput {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String content;

    // Optional: tags, priority, etc.

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
