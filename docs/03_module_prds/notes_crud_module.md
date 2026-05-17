# Notes CRUD Module PRD — Agent 2

## Scope
ONLY implement: Note entity, NoteRepository, NoteRequest, NoteResponse, NoteService (CRUD only), NoteController (CRUD only).

DO NOT touch: sharing logic (Agent 3), pin toggle (Agent 3), auth/security config (Agent 1).

---

## GLOBAL ENGINEERING RULES APPLY (see master_prd.md)

---

## Files to Create

### 1. entity/Note.java
```
Fields:
  UUID id (auto UUID)
  String title (not null)
  String content (columnDefinition = "TEXT", not null)
  User owner (ManyToOne, not null)
  boolean pinned (default false)
  LocalDateTime createdAt
  LocalDateTime updatedAt
  Set<User> sharedWith (ManyToMany — table: note_shares)
  
Annotations:
  @PrePersist → set createdAt, updatedAt to now
  @PreUpdate → set updatedAt to now
  @ManyToMany(fetch = LAZY) → sharedWith
  @JoinTable(name="note_shares", joinColumns=@JoinColumn(name="note_id"),
             inverseJoinColumns=@JoinColumn(name="user_id"))
```

### 2. repository/NoteRepository.java
```
extends JpaRepository<Note, UUID>
Methods:
  List<Note> findByOwnerOrderByPinnedDescCreatedAtDesc(User owner)
  Optional<Note> findByIdAndOwner(UUID id, User owner)
```

### 3. dto/request/NoteRequest.java
```
@NotBlank(message="Title is required")
@Size(max=255, message="Title is too long (max 255)")
String title

@NotBlank(message="Content is required")
@Size(max=50000, message="Content is too long (max 50000)")
String content
```

### 4. dto/response/NoteResponse.java
```
Fields: UUID id, String title, String content, boolean pinned,
        LocalDateTime createdAt, LocalDateTime updatedAt
Static factory: NoteResponse.from(Note note)
```

### 5. service/NoteService.java — CRUD PART ONLY (MAX 200 lines)
```
getCurrentUser(Authentication auth): extract User from auth principal

getAllNotes(Authentication auth):
  - Get current user
  - Return notes where owner=user OR user is in sharedWith
  - Order: pinned first, then by createdAt DESC
  - Return List<NoteResponse>

getNoteById(UUID id, Authentication auth):
  - Get current user
  - Find note by id
  - Note not found → throw NoteNotFoundException (404)
  - User is not owner AND not in sharedWith → throw AccessDeniedException (403)
  - Return NoteResponse

createNote(NoteRequest request, Authentication auth):
  - Get current user
  - Trim title and content
  - Validate (title blank after trim → 400)
  - Create and save note with owner = current user
  - Return NoteResponse (201)

updateNote(UUID id, NoteRequest request, Authentication auth):
  - Get current user
  - Find note by id → 404 if not found
  - User is NOT owner → 403 (shared users cannot edit)
  - Trim + validate title and content
  - Update fields
  - Save and return NoteResponse

deleteNote(UUID id, Authentication auth):
  - Get current user
  - Find note by id → 404 if not found
  - User is NOT owner → 403
  - Delete note
  - Return 204 No Content
```

### 6. controller/NoteController.java — CRUD PART (MAX 200 lines)
```
@RestController
@RequestMapping("/notes")
GET / → 200 List<NoteResponse>
GET /{id} → 200 NoteResponse
POST / → 201 NoteResponse
PUT /{id} → 200 NoteResponse
DELETE /{id} → 204

All methods: inject Authentication from Spring Security context
Use @Valid on request bodies
UUID path variable → catch invalid format (400)
```

---

## Edge Cases
- Get note owned by another user → 403 (not 404, because note exists)
- Get shared note → 200 (shared user can read)
- Update/delete shared note → 403 (only owner can modify)
- Whitespace-only title/content → 400
- Title > 255 chars → 400
- Content > 50000 chars → 400
- Invalid UUID in path → 400 "Invalid note ID format"
- Empty notes list → return [] (not 404)
- Non-existent note id → 404
