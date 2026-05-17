# Integration Rules — Multi-Agent Contract

These rules prevent integration collapse when multiple agents work simultaneously.

---

## Rule 1 — API Contract is Frozen

Once an endpoint's request/response schema is defined in `api_contracts.md`, it is **LOCKED**.

```
✅ ALLOWED:
  Adding new optional response fields (backward compatible)
  Adding new endpoints

❌ NOT ALLOWED without explicit approval:
  Renaming existing response fields
  Changing HTTP method of an endpoint
  Changing URL path of an endpoint
  Removing fields from responses
  Changing required fields to optional or vice versa
```

---

## Rule 2 — Entity Relationships are Frozen

Once defined in `database_schema.md`, entity relationships are immutable.

```
FROZEN:
  User (id, email, password, createdAt)
  Note (id, title, content, owner_id FK, pinned, createdAt, updatedAt)
  note_shares (note_id FK, user_id FK, shared_at)

❌ Agents must NOT:
  Add new entity fields without updating database_schema.md
  Change FK relationships
  Rename columns
  Change column types
```

---

## Rule 3 — Shared DTOs are Frozen

```
FROZEN DTOS (field names and types must not change):

NoteResponse:
  UUID id
  String title
  String content
  boolean pinned
  LocalDateTime createdAt
  LocalDateTime updatedAt

ErrorResponse:
  int status
  String error
  String message
  LocalDateTime timestamp

AuthResponse:
  String accessToken   (JSON: "access_token")

MessageResponse:
  String message
```

---

## Rule 4 — Service Layer Isolation

Controllers MUST NOT contain business logic. Services MUST NOT contain HTTP/web concerns.

```java
// ✅ Correct
// Controller: receives HTTP input, delegates to service, returns ResponseEntity
@PostMapping
public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest req, Authentication auth) {
    return ResponseEntity.status(201).body(noteService.createNote(req, auth));
}

// Service: contains all business logic, throws domain exceptions
public NoteResponse createNote(NoteRequest request, Authentication auth) {
    User user = getUser(auth);
    // ... business logic
}

// ❌ Wrong
// Controller doing business logic
@PostMapping
public ResponseEntity<?> createNote(@RequestBody NoteRequest req, Authentication auth) {
    User user = userRepository.findByEmail(auth.getName()).orElseThrow(...); // ← wrong layer
    Note note = new Note();
    // ... business logic in controller
}
```

---

## Rule 5 — No Cross-Agent File Modification

See `file_ownership_rules.md` for exact file ownership.

Summary:
- Agent 1 files: auth, security, JWT, User entity
- Agent 2 files: Note entity, note CRUD
- Agent 3 files: sharing, pinning (ADDS to existing files only)
- Agent 4 files: openapi, about, deployment
- Agent 5 files: exceptions, global handler (CREATED FIRST)

**If an agent needs something from another agent's file, it reads it — never modifies it.**

---

## Rule 6 — Repository Interface Contracts

Repository method signatures are locked once defined. Agents that use repositories call existing methods:

```java
// UserRepository — LOCKED
Optional<User> findByEmail(String email)
boolean existsByEmail(String email)

// NoteRepository — LOCKED
List<Note> findAllAccessibleByUser(User user)
Page<Note> findAllAccessibleByUserPaged(User user, Pageable pageable)
List<Note> searchAccessibleNotes(User user, String q)
Optional<Note> findByIdAndOwner(UUID id, User owner)
```

---

## Rule 7 — Error Handling Contract

All agents must use the exceptions defined by Agent 5. Never create ad-hoc exceptions inline.

```java
// ✅ Correct — use domain exceptions
throw new NoteNotFoundException("Note not found");
throw new NoteAccessDeniedException("Access denied");
throw new EmailAlreadyExistsException("Email already registered");

// ❌ Wrong — never do this
throw new RuntimeException("Note not found");
return ResponseEntity.status(404).body("not found");
```

---

## Rule 8 — No Hardcoded Secrets

All agents must use `@Value` or `${ENV_VAR}` for secrets:

```java
// ✅ Correct
@Value("${jwt.secret}")
private String secret;

// ❌ Wrong — never
private String secret = "hardcoded_secret_here";
```
