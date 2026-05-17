package com.abhinay.notesapp.dto.response;

import com.abhinay.notesapp.entity.Note;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        String title,
        String content,
        boolean pinned,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.isPinned(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
