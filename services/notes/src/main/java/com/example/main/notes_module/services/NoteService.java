package com.example.main.notes_module.services;

import com.example.main.notes_module.entities.Note;
import com.example.main.notes_module.repositor.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    // Get all note IDs by user ID
    public List<UUID> getNoteIdsByUserId(UUID userId) {
        return noteRepository.findByUserId(userId).stream()
                .map(Note::getId)
                .collect(Collectors.toList());
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

    // Update a note by ID
    public Note updateNote(UUID noteId, Note noteDetails) {
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isPresent()) {
            Note existingNote = optionalNote.get();
            existingNote.setTitle(noteDetails.getTitle());
            existingNote.setContent(noteDetails.getContent());
            existingNote.setUpdatedAt(LocalDate.now());
            existingNote.setShearedPeopleIds(noteDetails.getShearedPeopleIds());
            return noteRepository.save(existingNote);
        } else {
            throw new RuntimeException("Note not found");
        }
    }

    // Archive a note
    public Note archiveNote(UUID noteId) {
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            note.setArchived(true);
            note.setArchivedAt(LocalDate.now());
            return noteRepository.save(note);
        } else {
            throw new RuntimeException("Note not found");
        }
    }

    // Get all archived notes for a user
    public List<Note> getArchivedNotesByUserId(UUID userId) {
        return noteRepository.findByUserIdAndArchivedTrue(userId);
    }
}
