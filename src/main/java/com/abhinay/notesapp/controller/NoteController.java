package com.abhinay.notesapp.controller;

import com.abhinay.notesapp.dto.request.NoteRequest;
import com.abhinay.notesapp.dto.request.ShareRequest;
import com.abhinay.notesapp.dto.response.MessageResponse;
import com.abhinay.notesapp.dto.response.NoteResponse;
import com.abhinay.notesapp.dto.response.PinResponse;
import com.abhinay.notesapp.exception.BadRequestException;
import com.abhinay.notesapp.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "Notes CRUD, sharing, and pinning")
@SecurityRequirement(name = "BearerAuth")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    @Operation(summary = "List accessible notes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notes returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getAllNotes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication auth) {
        if (page != null || size != null) {
            if (page != null && page < 0) {
                throw new BadRequestException("Page number must be 0 or greater");
            }
            if (size != null && size < 1) {
                throw new BadRequestException("Page size must be at least 1");
            }
            if (size != null && size > 100) {
                throw new BadRequestException("Page size must not exceed 100");
            }
            int p = page != null ? page : 0;
            int s = size != null ? size : 10;
            Page<NoteResponse> paged = noteService.getAllNotesPaged(auth, PageRequest.of(p, s));
            return ResponseEntity.ok(paged);
        }
        List<NoteResponse> notes = noteService.getAllNotes(auth);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a note by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note returned"),
            @ApiResponse(responseCode = "400", description = "Invalid note ID"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(noteService.getNoteById(id, auth));
    }

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Create a note")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Note created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.createNote(request, auth));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    @Operation(summary = "Update a note")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<NoteResponse> updateNote(@PathVariable UUID id,
                                                   @Valid @RequestBody NoteRequest request,
                                                   Authentication auth) {
        return ResponseEntity.ok(noteService.updateNote(id, request, auth));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a note")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Note deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<Void> deleteNote(@PathVariable UUID id, Authentication auth) {
        noteService.deleteNote(id, auth);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/share", consumes = "application/json")
    @Operation(summary = "Share a note")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note shared"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User or note not found"),
            @ApiResponse(responseCode = "409", description = "Already shared")
    })
    public ResponseEntity<MessageResponse> shareNote(@PathVariable UUID id,
                                                     @Valid @RequestBody ShareRequest request,
                                                     Authentication auth) {
        return ResponseEntity.ok(noteService.shareNote(id, request, auth));
    }

    @PatchMapping("/{id}/pin")
    @Operation(summary = "Toggle note pin state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pin state changed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Note not found")
    })
    public ResponseEntity<PinResponse> togglePin(@PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(noteService.togglePin(id, auth));
    }
}
