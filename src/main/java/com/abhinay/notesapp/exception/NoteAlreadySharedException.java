package com.abhinay.notesapp.exception;

public class NoteAlreadySharedException extends RuntimeException {
    public NoteAlreadySharedException(String message) { super(message); }
}
