package com.abhinay.notesapp.repository;

import com.abhinay.notesapp.entity.Note;
import com.abhinay.notesapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    // All notes user owns OR is shared with, pinned first, then newest
    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.sharedWith sw " +
           "WHERE n.owner = :user OR sw = :user " +
           "ORDER BY n.pinned DESC, n.createdAt DESC")
    List<Note> findAllAccessibleByUser(@Param("user") User user);

    // Paginated version
    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.sharedWith sw " +
           "WHERE n.owner = :user OR sw = :user " +
           "ORDER BY n.pinned DESC, n.createdAt DESC")
    Page<Note> findAllAccessibleByUserPaged(@Param("user") User user, Pageable pageable);

    // Full-text search
    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.sharedWith sw " +
           "WHERE (n.owner = :user OR sw = :user) " +
           "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY n.pinned DESC, n.createdAt DESC")
    List<Note> searchAccessibleNotes(@Param("user") User user, @Param("q") String q);

    Optional<Note> findByIdAndOwner(UUID id, User owner);
}
