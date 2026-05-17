package com.abhinay.notesapp.exception;

public class NoteAccessDeniedException extends RuntimeException {
    public NoteAccessDeniedException(String message) { super(message); }
}
