package com.abhinay.notesapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title is too long (max 255)")
        String title,

        @NotBlank(message = "Content is required")
        @Size(max = 50000, message = "Content is too long (max 50000)")
        String content
) {}
