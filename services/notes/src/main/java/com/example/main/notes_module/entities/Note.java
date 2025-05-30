package com.example.main.notes_module.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "note")
public class Note {

    @Id
    private UUID id;

    private String authorId;

    private String title;

    private String content;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private boolean isArchived;

    private LocalDate archivedAt;

    private List<String> shearedPeopleIds;


}