package com.abhinay.notesapp.controller;

import com.abhinay.notesapp.dto.response.NoteResponse;
import com.abhinay.notesapp.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Search", description = "Search accessible notes")
public class SearchController {

    private final NoteService noteService;

    public SearchController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search notes", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<List<NoteResponse>> search(
            @RequestParam(required = false) String q,
            Authentication auth) {
        return ResponseEntity.ok(noteService.searchNotes(q, auth));
    }
}
