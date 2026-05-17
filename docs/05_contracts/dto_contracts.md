# DTO Contracts — Frozen Field Definitions

Every agent MUST use EXACTLY these field names, types, and annotations.
DO NOT invent new DTO structures. DO NOT rename fields.

---

## REQUEST DTOs

### RegisterRequest
```java
public record RegisterRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Size(max = 100, message = "Password too long")
    String password
) {}
```
JSON in: `{ "email": "string", "password": "string" }`

---

### LoginRequest
```java
public record LoginRequest(
    @NotBlank(message = "Email is required")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {}
```
JSON in: `{ "email": "string", "password": "string" }`

---

### NoteRequest (create + update)
```java
public record NoteRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title is too long (max 255)")
    String title,

    @NotBlank(message = "Content is required")
    @Size(max = 50000, message = "Content is too long (max 50000)")
    String content
) {}
```
JSON in: `{ "title": "string", "content": "string" }`

---

### ShareRequest
```java
public record ShareRequest(
    @JsonProperty("share_with_email")        // ← snake_case per spec
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    String shareWithEmail
) {}
```
JSON in: `{ "share_with_email": "string" }`  ← NOTE: snake_case in JSON

---

## RESPONSE DTOs

### AuthResponse
```java
public record AuthResponse(
    @JsonProperty("access_token")            // ← snake_case per spec
    String accessToken
) {}
```
JSON out: `{ "access_token": "eyJ..." }`

---

### NoteResponse ← FROZEN — DO NOT ADD/REMOVE FIELDS
```java
public record NoteResponse(
    UUID id,
    String title,
    String content,
    boolean pinned,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(
            note.getId(),
            note.getTitle(),
            note.getContent(),
            note.isPinned(),
            note.getCreatedAt(),
            note.getUpdatedAt()
        );
    }
}
```
JSON out:
```json
{
  "id": "uuid-string",
  "title": "string",
  "content": "string",
  "pinned": false,
  "createdAt": "2026-01-01T10:00:00",
  "updatedAt": "2026-01-01T10:00:00"
}
```

---

### ErrorResponse ← FROZEN — DO NOT ADD/REMOVE FIELDS
```java
public record ErrorResponse(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, LocalDateTime.now());
    }
}
```
JSON out:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Title is required",
  "timestamp": "2026-01-01T10:00:00"
}
```

---

### MessageResponse
```java
public record MessageResponse(String message) {}
```
JSON out: `{ "message": "string" }`

---

### PinResponse
```java
public record PinResponse(String message, boolean pinned) {}
```
JSON out: `{ "message": "Note pinned", "pinned": true }`

---

## JSON Serialization Rules

- `LocalDateTime` serializes as ISO 8601: `"2026-01-01T10:00:00"` (no timezone suffix)
- `UUID` serializes as lowercase hyphenated string: `"550e8400-e29b-41d4-a716-446655440000"`
- `boolean` serializes as `true`/`false` (not `"true"`/`"false"`)
- `null` fields: configure Jackson to NOT serialize null fields (`@JsonInclude(NON_NULL)` if needed)
- Field names: camelCase in JSON by default EXCEPT where `@JsonProperty` overrides (access_token, share_with_email)

---

## DTO Rules — Agents Must Follow

```
✅ Use records (Java 16+) for all DTOs
✅ Use @Valid at controller method parameter level
✅ Use @JsonProperty only for spec-required snake_case fields
✅ Use static factory method from(Entity) on response DTOs
✅ All response DTOs are immutable (records)

❌ Never return an Entity directly from a controller
❌ Never add @JsonIgnore to hide sensitive fields — use DTOs instead
❌ Never modify frozen DTO field names (NoteResponse, ErrorResponse)
❌ Never add mutable state to DTO records
```
