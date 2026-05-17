# Naming Conventions

Consistent naming across all AI-generated code. Every agent MUST follow these.

---

## Java — Classes (PascalCase)

| Type | Convention | Examples |
|---|---|---|
| Entity | `EntityName` | `User`, `Note` |
| Repository | `EntityNameRepository` | `UserRepository`, `NoteRepository` |
| Service | `EntityNameService` | `AuthService`, `NoteService` |
| Controller | `EntityNameController` | `AuthController`, `NoteController` |
| Request DTO | `ActionNameRequest` | `RegisterRequest`, `NoteRequest`, `ShareRequest` |
| Response DTO | `EntityNameResponse` | `NoteResponse`, `AuthResponse`, `ErrorResponse` |
| Filter | `DescriptiveNameFilter` | `JwtAuthFilter` |
| Config | `DescriptiveNameConfig` | `SecurityConfig`, `OpenApiConfig` |
| Util | `DescriptiveNameUtil` | `JwtUtil` |
| Exception | `DescriptiveNameException` | `NoteNotFoundException`, `EmailAlreadyExistsException` |

---

## Java — Fields & Variables (camelCase)

```java
// ✅ Correct
private UUID id;
private String email;
private LocalDateTime createdAt;
private boolean isPinned;
private Set<User> sharedWith;

// ❌ Wrong
private UUID ID;
private String Email;
private LocalDateTime created_at;
private boolean is_pinned;
```

---

## Java — Methods (camelCase, verb-first)

```java
// ✅ Correct
public NoteResponse createNote(...)
public NoteResponse updateNote(...)
public void deleteNote(...)
public MessageResponse shareNote(...)
public PinResponse togglePin(...)
public List<NoteResponse> getAllNotes(...)
public NoteResponse getNoteById(...)

// ❌ Wrong
public NoteResponse NoteCreate(...)
public NoteResponse note_update(...)
```

---

## Java — Constants (UPPER_SNAKE_CASE)

```java
// ✅ Correct
private static final String INVALID_CREDENTIALS = "Invalid email or password";
private static final int MAX_PAGE_SIZE = 100;
```

---

## Database — Tables (snake_case, plural)

```sql
-- ✅ Correct
users
notes
note_shares

-- ❌ Wrong
User
Notes
NoteShares
note_share
```

---

## Database — Columns (snake_case)

```sql
-- ✅ Correct
id, email, password, created_at, updated_at, owner_id, note_id, user_id

-- ❌ Wrong
ID, Email, createdAt, ownerId
```

---

## JSON Fields — camelCase (Spring default)

```json
{
  "accessToken": "...",
  "createdAt": "...",
  "updatedAt": "...",
  "shareWithEmail": "..."
}
```

**Exception:** `share_with_email` in ShareRequest uses `@JsonProperty("share_with_email")` to match the assignment spec.

---

## Package Naming (lowercase)

```
com.abhinay.notesapp
com.abhinay.notesapp.config
com.abhinay.notesapp.controller
com.abhinay.notesapp.dto.request
com.abhinay.notesapp.dto.response
com.abhinay.notesapp.entity
com.abhinay.notesapp.exception
com.abhinay.notesapp.filter
com.abhinay.notesapp.repository
com.abhinay.notesapp.service
com.abhinay.notesapp.util
```

---

## File Naming

| Type | Convention |
|---|---|
| Java files | Match class name exactly: `NoteService.java` |
| YAML config | `application.yml` (never `application.yaml`) |
| PRD docs | `snake_case.md` |
| Frontend | `notes-app-ui.html` |
