package com.example.main.notes_module.controllers;

import com.example.main.notes_module.models.Note;
import com.example.main.notes_module.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NoteService noteService;

    // Get all note IDs for a user
    @GetMapping("/user/{userId}/ids")
    public List<UUID> getUserNoteIds(@PathVariable UUID userId) {
        return noteService.getNoteIdsByUserId(userId);
    }

    // Get a single note by ID
    @GetMapping("/{noteId}")
    public Optional<Note> getNoteById(@PathVariable UUID noteId) {
        return noteService.getNoteById(noteId);
    }

    // Delete a note by ID
    @DeleteMapping("/{noteId}")
    public void deleteNote(@PathVariable UUID noteId) {
        noteService.deleteNote(noteId);
    }

    // Update a note
    @PutMapping("/{noteId}")
    public Note updateNote(@PathVariable UUID noteId, @Valid @RequestBody Note note) {
        return noteService.updateNote(noteId, note);
    }

    // Archive a note
    @PostMapping("/{noteId}/archive")
    public Note archiveNote(@PathVariable UUID noteId) {
        return noteService.archiveNote(noteId);
    }

    // Get archived notes for a user
    @GetMapping("/user/{userId}/archived")
    public List<Note> getArchivedNotes(@PathVariable UUID userId) {
        return noteService.getArchivedNotesByUserId(userId);
    }
}
