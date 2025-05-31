package com.example.main.notes_module.controllers;

import com.example.main.notes_module.entities.Note;
import com.example.main.notes_module.inputs.CreateNoteInput;
import com.example.main.notes_module.inputs.UpdateNoteInput;
import com.example.main.notes_module.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<Note> getNoteById(@PathVariable UUID noteId) {
        return noteService.getNoteById(noteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a note by ID
    @DeleteMapping("/{noteId}")
    public void deleteNote(@PathVariable UUID noteId) {
        noteService.deleteNote(noteId);
    }

    // Update a note
    @PutMapping("/{noteId}")
    public ResponseEntity<Note> updateNote(@PathVariable UUID noteId, @Valid @RequestBody UpdateNoteInput note) {
        return ResponseEntity.ok(noteService.updateNote(noteId, note));
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

    // Create a new note
    @PostMapping
    public ResponseEntity<Note> createNote(@Valid @RequestBody CreateNoteInput input) {
        return ResponseEntity.ok(noteService.createNote(input));
    }
}
