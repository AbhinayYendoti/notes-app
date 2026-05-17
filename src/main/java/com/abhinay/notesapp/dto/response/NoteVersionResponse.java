package com.abhinay.notesapp.dto.response;

import com.abhinay.notesapp.entity.NoteVersion;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteVersionResponse(
        UUID id,
        String title,
        String content,
        int versionNumber,
        LocalDateTime savedAt
) {
    public static NoteVersionResponse from(NoteVersion version) {
        return new NoteVersionResponse(
                version.getId(),
                version.getTitle(),
                version.getContent(),
                version.getVersionNumber(),
                version.getSavedAt()
        );
    }
}
