# Logging Rules

Production-oriented logging standards. Defines what to log, what never to log, and how.

---

## Setup (Every Service and Filter)

```java
// Add to every class that needs logging
@Slf4j
public class AuthService { ... }

// Usage
log.info("User registered: {}", email);
log.warn("Failed login attempt for email: {}", email);
log.error("Unexpected error in createNote: {}", e.getMessage(), e);
```

Always use SLF4J (`@Slf4j` from Lombok). Never use `System.out.println`.

---

## What MUST Be Logged

### Auth Events (WARN level — security-relevant)
```java
// Successful registration
log.info("New user registered: {}", email);

// Failed login
log.warn("Failed login attempt for email: {}", email);

// JWT validation failure
log.warn("Invalid JWT token: {}", e.getMessage());

// Expired JWT
log.warn("Expired JWT token for request: {}", requestURI);
```

### Business Events (INFO level)
```java
// Note created
log.info("Note created: noteId={}, owner={}", note.getId(), ownerEmail);

// Note deleted
log.info("Note deleted: noteId={}, by={}", noteId, requesterEmail);

// Note shared
log.info("Note shared: noteId={}, owner={}, sharedWith={}", noteId, ownerEmail, targetEmail);
```

### Errors (ERROR level — unexpected failures)
```java
// In GlobalExceptionHandler catch-all
log.error("Unhandled exception: {}", e.getMessage(), e);
```

---

## What MUST NEVER Be Logged

```
❌ Raw passwords (plaintext or hashed)
❌ JWT token strings
❌ Full stack traces in INFO/WARN (only in ERROR)
❌ DB connection strings or credentials
❌ Full request bodies (may contain passwords)
❌ User's note content (privacy)
```

```java
// ❌ WRONG
log.info("Login attempt: email={}, password={}", email, password);

// ✅ CORRECT
log.warn("Failed login attempt for email: {}", email);
```

---

## Log Level Guide

| Level | When to Use |
|---|---|
| `ERROR` | Unhandled exceptions, DB errors, startup failures |
| `WARN` | Auth failures, JWT errors, 403/404 patterns, suspicious activity |
| `INFO` | Successful business operations (register, create, share, delete) |
| `DEBUG` | Detailed flow info — only active in development (not production) |

---

## application.yml Logging Config

```yaml
logging:
  level:
    root: INFO
    com.abhinay.notesapp: INFO
    org.springframework.security: WARN   # reduce Spring Security noise
    org.hibernate.SQL: DEBUG             # see SQL in dev only — remove in prod
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

---

## Log Format in Production (Render)

Render captures stdout. The pattern above produces:
```
2026-01-01 10:00:00 [main] INFO  c.a.n.service.AuthService - New user registered: test@example.com
2026-01-01 10:00:01 [main] WARN  c.a.n.service.AuthService - Failed login attempt for email: hacker@test.com
2026-01-01 10:00:02 [main] ERROR c.a.n.e.GlobalExceptionHandler - Unhandled exception: Connection refused
```

This format lets you debug issues on Render by reading deployment logs.
