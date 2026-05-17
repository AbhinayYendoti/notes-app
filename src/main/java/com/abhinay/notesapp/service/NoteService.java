package com.abhinay.notesapp.service;

import com.abhinay.notesapp.dto.request.NoteRequest;
import com.abhinay.notesapp.dto.request.ShareRequest;
import com.abhinay.notesapp.dto.response.MessageResponse;
import com.abhinay.notesapp.dto.response.NoteResponse;
import com.abhinay.notesapp.dto.response.NoteVersionResponse;
import com.abhinay.notesapp.dto.response.PinResponse;
import com.abhinay.notesapp.entity.Note;
import com.abhinay.notesapp.entity.NoteVersion;
import com.abhinay.notesapp.entity.User;
import com.abhinay.notesapp.exception.*;
import com.abhinay.notesapp.repository.NoteRepository;
import com.abhinay.notesapp.repository.NoteVersionRepository;
import com.abhinay.notesapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;
    private final NoteVersionRepository noteVersionRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository,
                       NoteVersionRepository noteVersionRepository,
                       UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.noteVersionRepository = noteVersionRepository;
        this.userRepository = userRepository;
    }

    private User getUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new InvalidTokenException("Unauthorized");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Note getNoteOrThrow(UUID id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));
    }

    private void assertOwner(Note note, User user) {
        if (!note.getOwner().getId().equals(user.getId())) {
            throw new NoteAccessDeniedException("Access denied");
        }
    }

    private void assertAccess(Note note, User user) {
        boolean isOwner = note.getOwner().getId().equals(user.getId());
        boolean isShared = note.getSharedWith().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        if (!isOwner && !isShared) {
            throw new NoteAccessDeniedException("Access denied");
        }
    }

    private void saveVersionSnapshot(Note note) {
        NoteVersion version = new NoteVersion();
        version.setNote(note);
        version.setTitle(note.getTitle());
        version.setContent(note.getContent());
        version.setVersionNumber(noteVersionRepository.countByNote(note) + 1);
        noteVersionRepository.save(version);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAllNotes(Authentication auth) {
        User user = getUser(auth);
        return noteRepository.findAllAccessibleByUser(user)
                .stream().map(NoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Page<NoteResponse> getAllNotesPaged(Authentication auth, Pageable pageable) {
        User user = getUser(auth);
        return noteRepository.findAllAccessibleByUserPaged(user, pageable)
                .map(NoteResponse::from);
    }

    @Transactional(readOnly = true)
    public NoteResponse getNoteById(UUID id, Authentication auth) {
        User user = getUser(auth);
        Note note = getNoteOrThrow(id);
        assertAccess(note, user);
        return NoteResponse.from(note);
    }

    @Transactional
    public NoteResponse createNote(NoteRequest request, Authentication auth) {
        User user = getUser(auth);
        Note note = new Note();
        note.setTitle(request.title().trim());
        note.setContent(request.content().trim());
        note.setOwner(user);
        return NoteResponse.from(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse updateNote(UUID id, NoteRequest request, Authentication auth) {
        User user = getUser(auth);
        Note note = getNoteOrThrow(id);
        assertOwner(note, user);
        saveVersionSnapshot(note);
        note.setTitle(request.title().trim());
        note.setContent(request.content().trim());
        return NoteResponse.from(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<NoteVersionResponse> getHistory(UUID noteId, Authentication auth) {
        User user = getUser(auth);
        Note note = getNoteOrThrow(noteId);
        assertOwner(note, user);
        return noteVersionRepository.findByNoteOrderByVersionNumberDesc(note)
                .stream().map(NoteVersionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public NoteVersionResponse getVersion(UUID noteId, UUID versionId, Authentication auth) {
        User user = getUser(auth);
        Note note = getNoteOrThrow(noteId);
        assertOwner(note, user);
        NoteVersion version = noteVersionRepository.findByIdAndNote(versionId, note)
                .orElseThrow(() -> new NoteNotFoundException("Version not found"));
        return NoteVersionResponse.from(version);
    }

    @Transactional
    public NoteResponse restoreVersion(UUID noteId, UUID versionId, Authentication auth) {
        User user = getUser(auth);
        Note note = getNoteOrThrow(noteId);
        assertOwner(note, user);
        NoteVersion version = noteVersionRepository.findByIdAndNote(versionId, note)
                .orElseThrow(() -> new NoteNotFoundException("Version not found"));

        saveVersionSnapshot(note);
        note.setTitle(version.getTitle());
        note.setContent(version.getContent());
        Note restored = noteRepository.save(note);
        log.info("Restored note {} to version {}", noteId, version.getVersionNumber());
        return NoteResponse.from(restored);
    }

    @Transactional
    public void deleteNote(UUID id, Authentication auth) {
        User user = getUser(auth);
        Note note = getNoteOrThrow(id);
        assertOwner(note, user);
        noteRepository.delete(note);
    }

    @Transactional
    public MessageResponse shareNote(UUID noteId, ShareRequest request, Authentication auth) {
        User owner = getUser(auth);
        Note note = getNoteOrThrow(noteId);
        assertOwner(note, owner);

        String targetEmail = request.shareWithEmail().trim().toLowerCase();

        if (targetEmail.equals(owner.getEmail())) {
            throw new BadRequestException("Cannot share a note with yourself");
        }

        User target = userRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean alreadyShared = note.getSharedWith().stream()
                .anyMatch(u -> u.getId().equals(target.getId()));
        if (alreadyShared) {
            throw new NoteAlreadySharedException("Note already shared with this user");
        }

        note.getSharedWith().add(target);
        noteRepository.save(note);
        return new MessageResponse("Note shared successfully");
    }

    @Transactional
    public PinResponse togglePin(UUID noteId, Authentication auth) {
        User user = getUser(auth);
        Note note = getNoteOrThrow(noteId);
        assertOwner(note, user);
        note.setPinned(!note.isPinned());
        noteRepository.save(note);
        String msg = note.isPinned() ? "Note pinned" : "Note unpinned";
        return new PinResponse(msg, note.isPinned());
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> searchNotes(String q, Authentication auth) {
        User user = getUser(auth);
        if (q == null || q.trim().isEmpty()) {
            return getAllNotes(auth);
        }
        return noteRepository.searchAccessibleNotes(user, q.trim())
                .stream().map(NoteResponse::from).toList();
    }
}
