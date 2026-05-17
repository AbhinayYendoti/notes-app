package com.abhinay.notesapp.repository;

import com.abhinay.notesapp.entity.Note;
import com.abhinay.notesapp.entity.NoteVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteVersionRepository extends JpaRepository<NoteVersion, UUID> {
    List<NoteVersion> findByNoteOrderByVersionNumberDesc(Note note);
    int countByNote(Note note);
    Optional<NoteVersion> findByIdAndNote(UUID id, Note note);
}
