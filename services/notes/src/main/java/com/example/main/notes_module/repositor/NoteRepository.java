package com.example.main.notes_module.repositor;

import com.example.main.notes_module.entities.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends MongoRepository<Note, UUID> {

    // Get all notes by user
    List<Note> findByUserId(UUID userId);

    // Get all archived notes by user
    List<Note> findByUserIdAndIsArchivedTrue(UUID userId);

    // Get a specific note by ID
    Optional<Note> findById(UUID noteId);

    // Get all active (non-archived) notes
    List<Note> findByAuthorIdAndIsArchivedFalse(UUID userId);

    // Save or update note
    default Note saveNote(Note note) {
        return save(note);
    }

    // Delete note
    default void deleteNote(Note note) {
        delete(note);
    }

    // Delete by ID
    default void deleteNoteById(UUID noteId) {
        deleteById(noteId);
    }

    // Check existence by ID
    boolean existsById(UUID noteId);
}
