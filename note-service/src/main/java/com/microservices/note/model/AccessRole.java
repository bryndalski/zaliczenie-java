package com.microservices.note.model;

public enum AccessRole {
    AUTHOR,    // Can read, update, delete
    EDITOR,    // Can read and update
    SPECTATOR  // Can only read
}
