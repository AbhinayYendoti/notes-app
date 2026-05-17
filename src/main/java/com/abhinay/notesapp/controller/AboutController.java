package com.abhinay.notesapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "About", description = "Project metadata")
public class AboutController {

    @GetMapping("/about")
    @Operation(summary = "Get project metadata")
    public ResponseEntity<Map<String, Object>> about() {
        Map<String, String> features = Map.of(
                "Note Pinning", "Users can pin important notes so they always appear at the top of the notes list. Implemented PATCH /notes/{id}/pin. Chosen because it mirrors real-world note apps like Google Keep and demonstrates product-sense driven feature design.",
                "Full-Text Search", "GET /search?q=keyword searches notes by title and content, returning only notes the authenticated user owns or has access to. Chosen to demonstrate database query design and real-world utility.",
                "Note Version History", "Every note update automatically stores the previous state as an immutable snapshot. Users can inspect edit history and restore an older version safely. Restoring also snapshots the current state first, preventing data loss. I chose this because it demonstrates transaction safety, auditability, and rollback design rather than a simple CRUD extension."
        );
        return ResponseEntity.ok(Map.of(
                "name", "Abhinay Yendoti",
                "email", "abhinayyendoti@gmail.com",
                "my features", features,
                "my_features", features
        ));
    }
}
