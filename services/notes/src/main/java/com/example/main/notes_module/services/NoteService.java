package com.example.main.notes_module.services;

import com.example.main.notes_module.entities.Note;
import com.example.main.notes_module.inputs.CreateNoteInput;
import com.example.main.notes_module.inputs.UpdateNoteInput;
import com.example.main.notes_module.repositor.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    // Get all note IDs by user ID
    public List<UUID> getNoteIdsByUserId(UUID userId) {
        return noteRepository.findByUserId(userId).stream()
                .map(Note::getId)
                .toList();
    }

    // Get a note by its ID
    public Optional<Note> getNoteById(UUID noteId) {
        return noteRepository.findById(noteId);
    }

    // Delete a note by its ID
    public void deleteNote(UUID noteId) {
        if (noteRepository.existsById(noteId)) {
            noteRepository.deleteById(noteId);
        } else {
            throw new RuntimeException("Note not found");
        }
    }

    public Note createNote(CreateNoteInput input) {
        Note note = Note.builder()
                .authorId(input.getUserId())
                .title(input.getTitle())
                .content(input.getContent())
                .build();
        return noteRepository.save(note);
    }

    // Update a note by ID
    @Transactional
    public Note updateNote(UUID noteId, UpdateNoteInput noteDetails) {
        var existingNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoSuchElementException("Note not found with id: " + noteId));

        existingNote.setTitle(noteDetails.getTitle());
        existingNote.setContent(noteDetails.getContent());
        existingNote.setUpdatedAt(LocalDate.now());

        return noteRepository.save(existingNote);
    }

    // Archive a note
    @Transactional
    public Note archiveNote(UUID noteId) {
        var note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setArchived(true);
        note.setArchivedAt(LocalDate.now());
        return noteRepository.save(note);
    }

    // Get all archived notes for a user
    public List<Note> getArchivedNotesByUserId(UUID userId) {
        return noteRepository.findByUserIdAndIsArchivedTrue(userId);
    }
}
