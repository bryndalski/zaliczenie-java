package com.microservices.note.service;

import com.microservices.note.dto.CreateNoteRequest;
import com.microservices.note.dto.UpdateNoteRequest;
import com.microservices.note.exception.NoteNotFoundException;
import com.microservices.note.exception.UnauthorizedAccessException;
import com.microservices.note.model.Note;
import com.microservices.note.repository.NoteRepository;
import com.microservices.note.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Transactional
public class NoteService {
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<Note> getAllUserNotes(String userId, Pageable pageable) {
        return noteRepository.findAllByUserId(userId, pageable);
    }
    
    public Note getNoteById(String noteId, String userId) {
        if (!hasReadAccess(noteId, userId)) {
            throw new UnauthorizedAccessException("No read access to this note");
        }
        
        return noteRepository.findById(noteId)
            .orElseThrow(() -> new NoteNotFoundException("Note not found"));
    }
    
    public Note createNote(CreateNoteRequest request, String userId, String email, String username) {
        // Ensure user exists in graph
        userRepository.createOrUpdateUser(userId, email, username);
        
        // Create note
        Note note = new Note(request.getTitle(), request.getContent());
        note = noteRepository.save(note);
        
        // Grant AUTHOR access to creator
        noteRepository.createUserAccess(userId, note.getId(), "AUTHOR");
        
        return note;
    }
    
    public Note updateNote(String noteId, UpdateNoteRequest request, String userId) {
        if (!hasWriteAccess(noteId, userId)) {
            throw new UnauthorizedAccessException("No write access to this note");
        }
        
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new NoteNotFoundException("Note not found"));
        
        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        
        note.updateTimestamp();
        return noteRepository.save(note);
    }
    
    public void deleteNote(String noteId, String userId) {
        if (!hasAuthorAccess(noteId, userId)) {
            throw new UnauthorizedAccessException("Only the author can delete this note");
        }
        
        if (!noteRepository.existsById(noteId)) {
            throw new NoteNotFoundException("Note not found");
        }
        
        noteRepository.deleteById(noteId);
    }
    
    // Access control methods
    private boolean hasReadAccess(String noteId, String userId) {
        return noteRepository.hasAccess(noteId, userId, 
            Arrays.asList("AUTHOR", "EDITOR", "SPECTATOR"));
    }
    
    private boolean hasWriteAccess(String noteId, String userId) {
        return noteRepository.hasAccess(noteId, userId, 
            Arrays.asList("AUTHOR", "EDITOR"));
    }
    
    private boolean hasAuthorAccess(String noteId, String userId) {
        return noteRepository.hasAccess(noteId, userId, 
            Arrays.asList("AUTHOR"));
    }
}
