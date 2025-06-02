package com.microservices.note.controller;

import com.microservices.note.dto.CreateNoteRequest;
import com.microservices.note.dto.UpdateNoteRequest;
import com.microservices.note.model.Note;
import com.microservices.note.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "Note management with role-based access control")
public class NoteController {
    
    @Autowired
    private NoteService noteService;
    
    @GetMapping
    @Operation(summary = "Get all user notes", description = "Retrieve all notes accessible to the authenticated user")
    @ApiResponse(responseCode = "200", description = "Notes retrieved successfully")
    public ResponseEntity<Page<Note>> getAllNotes(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        String userId = jwt.getClaimAsString("sub");
        Pageable pageable = PageRequest.of(page, size);
        Page<Note> notes = noteService.getAllUserNotes(userId, pageable);
        
        return ResponseEntity.ok(notes);
    }
    
    @PostMapping
    @Operation(summary = "Create note", description = "Create a new note with the authenticated user as author")
    @ApiResponse(responseCode = "200", description = "Note created successfully")
    public ResponseEntity<Note> createNote(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateNoteRequest request) {
        
        String userId = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");
        
        Note note = noteService.createNote(request, userId, email, username);
        return ResponseEntity.ok(note);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get note by ID", description = "Retrieve a specific note by ID")
    @ApiResponse(responseCode = "200", description = "Note found")
    @ApiResponse(responseCode = "404", description = "Note not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Note> getNoteById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {
        
        String userId = jwt.getClaimAsString("sub");
        Note note = noteService.getNoteById(id, userId);
        return ResponseEntity.ok(note);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update note", description = "Update a note (requires AUTHOR or EDITOR access)")
    @ApiResponse(responseCode = "200", description = "Note updated successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Note> updateNote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id,
            @Valid @RequestBody UpdateNoteRequest request) {
        
        String userId = jwt.getClaimAsString("sub");
        Note note = noteService.updateNote(id, request, userId);
        return ResponseEntity.ok(note);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note", description = "Delete a note (requires AUTHOR access)")
    @ApiResponse(responseCode = "200", description = "Note deleted successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    public ResponseEntity<Void> deleteNote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String id) {
        
        String userId = jwt.getClaimAsString("sub");
        noteService.deleteNote(id, userId);
        return ResponseEntity.ok().build();
    }
}
