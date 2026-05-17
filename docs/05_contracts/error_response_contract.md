# Error Response Contract

## Canonical Error Format (ALL endpoints, ALL errors)

```json
{
  "timestamp": "2026-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed"
}
```

### Field Rules
| Field | Type | Format | Notes |
|---|---|---|---|
| `timestamp` | String | ISO 8601 (`LocalDateTime.now()`) | Server time at error moment |
| `status` | Integer | HTTP status code | 400, 401, 403, 404, 409, 415, 500 |
| `error` | String | HTTP reason phrase | "Bad Request", "Unauthorized", etc. |
| `message` | String | Human-readable | Specific, actionable — never stack trace |

### Java DTO (DO NOT CHANGE)
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

---

## Full Status Code → Error String Mapping

| HTTP Status | `error` field value |
|---|---|
| 400 | `"Bad Request"` |
| 401 | `"Unauthorized"` |
| 403 | `"Forbidden"` |
| 404 | `"Not Found"` |
| 409 | `"Conflict"` |
| 415 | `"Unsupported Media Type"` |
| 500 | `"Internal Server Error"` |

---

## Message Rules

### ✅ Correct messages
```json
{ "message": "Email already registered" }
{ "message": "Password must be at least 8 characters" }
{ "message": "Note not found" }
{ "message": "Access denied" }
{ "message": "Cannot share a note with yourself" }
{ "message": "Note already shared with this user" }
{ "message": "Token expired" }
{ "message": "Invalid token" }
{ "message": "Invalid email or password" }
{ "message": "Malformed JSON request body" }
{ "message": "Invalid ID format" }
{ "message": "Internal server error" }
```

### ❌ NEVER return
```json
{ "message": "NullPointerException at line 47..." }
{ "message": "could not execute statement..." }
{ "message": "detached entity passed to persist..." }
{ "message": null }
```

---

## Validation Error Specifics

For `@Valid` failures → return the **FIRST field error only**:
```json
{
  "timestamp": "2026-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Password must be at least 8 characters"
}
```

NOT a list of errors — single message, most critical first.

---

## 500 Rule (STRICT)

For any unhandled exception:
```json
{
  "timestamp": "...",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Internal server error"
}
```

Log the actual exception server-side with `@Slf4j`. **NEVER expose it** in the response.

---

## Authentication Error Specifics

| Scenario | Status | message |
|---|---|---|
| Missing Authorization header | 401 | `"Unauthorized"` |
| Malformed Bearer token | 401 | `"Invalid token"` |
| Expired JWT | 401 | `"Token expired"` |
| Invalid JWT signature | 401 | `"Invalid token"` |
| Wrong email or password | 401 | `"Invalid email or password"` |

> **Security rule:** Login NEVER reveals whether the email exists. Always return the same message for wrong email AND wrong password.
