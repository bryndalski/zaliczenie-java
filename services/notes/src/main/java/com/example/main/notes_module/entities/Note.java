package com.example.main.notes_module.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@Document(collection = "note")
public class Note {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private UUID userId;  // Added this field

    @NotNull
    private UUID authorId;

    @NotNull
    private String title;

    private String content;

    @CreatedDate
    private LocalDate createdAt = LocalDate.now();

    @LastModifiedDate
    private LocalDate updatedAt = LocalDate.now();

    private boolean isArchived = false;

    private LocalDate archivedAt;

    private List<String> shearedPeopleIds = new ArrayList<>();
}