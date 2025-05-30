package com.example.main.notes_module.inputs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UpdateNoteInput {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Note ID is required")
    private UUID noteId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;
}
