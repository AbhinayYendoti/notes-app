package com.abhinay.notesapp.controller;

import com.abhinay.notesapp.dto.response.NoteResponse;
import com.abhinay.notesapp.dto.response.NoteVersionResponse;
import com.abhinay.notesapp.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes/{noteId}/history")
@Tag(name = "Note History", description = "Immutable note version history and restore")
@SecurityRequirement(name = "BearerAuth")
public class NoteVersionController {

    private final NoteService noteService;

    public NoteVersionController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    @Operation(summary = "List note version history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Version history returned"),
            @ApiResponse(responseCode = "403", description = "Only note owners can view history"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<List<NoteVersionResponse>> getHistory(
            @PathVariable UUID noteId,
            Authentication auth) {
        return ResponseEntity.ok(noteService.getHistory(noteId, auth));
    }

    @GetMapping("/{versionId}")
    @Operation(summary = "Get one note version")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Version returned"),
            @ApiResponse(responseCode = "403", description = "Only note owners can view history"),
            @ApiResponse(responseCode = "404", description = "Note or version not found")
    })
    public ResponseEntity<NoteVersionResponse> getVersion(
            @PathVariable UUID noteId,
            @PathVariable UUID versionId,
            Authentication auth) {
        return ResponseEntity.ok(noteService.getVersion(noteId, versionId, auth));
    }

    @PostMapping("/{versionId}/restore")
    @Operation(summary = "Restore a previous note version")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Version restored"),
            @ApiResponse(responseCode = "403", description = "Only note owners can restore versions"),
            @ApiResponse(responseCode = "404", description = "Note or version not found")
    })
    public ResponseEntity<NoteResponse> restore(
            @PathVariable UUID noteId,
            @PathVariable UUID versionId,
            Authentication auth) {
        return ResponseEntity.ok(noteService.restoreVersion(noteId, versionId, auth));
    }
}
