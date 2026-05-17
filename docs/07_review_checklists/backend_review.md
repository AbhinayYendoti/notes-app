# Backend Review Checklist — Pre-Submission Quality Gate

Run this checklist BEFORE deploying. Every box must be checked.

---

## Auth & JWT

- [ ] POST /register returns 201 with `{"message": "User registered successfully"}`
- [ ] POST /register with duplicate email returns 409 (not 500)
- [ ] POST /register with invalid email format returns 400
- [ ] POST /register with password < 8 chars returns 400
- [ ] POST /login returns 200 with `{"access_token": "..."}`
- [ ] POST /login with wrong password returns 401 — NOT 403, NOT 404
- [ ] POST /login with wrong email returns SAME 401 message as wrong password (no enumeration)
- [ ] Email is trimmed and lowercased before saving and lookup
- [ ] Password is NEVER stored in plaintext — BCrypt only
- [ ] JWT token contains email as subject
- [ ] JWT expiration is respected (86400000ms = 24h)
- [ ] Protected endpoints return 401 with no Authorization header
- [ ] Protected endpoints return 401 with expired JWT
- [ ] Protected endpoints return 401 with malformed JWT

---

## Ownership & Authorization

- [ ] GET /notes returns ONLY notes owned by or shared with the authenticated user
- [ ] GET /notes/{id} returns 403 if note exists but belongs to another user (NOT 404)
- [ ] PUT /notes/{id} returns 403 if requester is not the owner (shared users cannot edit)
- [ ] DELETE /notes/{id} returns 403 if requester is not the owner
- [ ] POST /notes/{id}/share returns 403 if requester is not the owner
- [ ] PATCH /notes/{id}/pin returns 403 if requester is not the owner
- [ ] No endpoint accidentally exposes another user's note data

---

## Validations

- [ ] All @NotBlank fields reject null, empty string, and whitespace-only strings
- [ ] Title max 255 chars enforced → 400
- [ ] Content max 50000 chars enforced → 400
- [ ] Email format validated with @Email annotation
- [ ] Password min 8 chars validated
- [ ] UUID path variables reject non-UUID strings → 400 (not 500)
- [ ] Malformed JSON body returns 400 (not 500)
- [ ] share_with_email validated: not blank, valid email format
- [ ] Cannot share with yourself → 400
- [ ] Cannot share with same user twice → 409
- [ ] Sharing with non-existent email → 404
- [ ] Pagination: page < 0 → 400, size < 1 or > 100 → 400

---

## HTTP Status Codes

- [ ] Register → 201
- [ ] Login success → 200
- [ ] Create note → 201
- [ ] Get notes / note by id / update → 200
- [ ] Delete note → 204 (NO body)
- [ ] Share note → 200
- [ ] Pin toggle → 200
- [ ] Not found → 404
- [ ] Unauthorized → 401
- [ ] Forbidden → 403
- [ ] Conflict (duplicate) → 409
- [ ] Validation error → 400
- [ ] Server error → 500 (sanitized message only)

---

## DTO Consistency

- [ ] NoteResponse always includes: id, title, content, pinned, createdAt, updatedAt
- [ ] ErrorResponse always includes: status, error, message, timestamp
- [ ] AuthResponse uses JSON key `access_token` (snake_case per spec)
- [ ] ShareRequest uses JSON key `share_with_email` (per spec)
- [ ] No entity class is ever returned directly from a controller
- [ ] LocalDateTime serialized as ISO 8601 string (not Unix timestamp)
- [ ] GET /notes returns array `[...]` when no pagination params, Page object when paginated

---

## Code Quality

- [ ] No single Java file exceeds 250 lines
- [ ] No business logic in controllers (controllers only delegate to services)
- [ ] No HTTP/web concerns in services (no HttpServletRequest, no ResponseEntity)
- [ ] No duplicated ownership-check logic (use assertOwner/assertAccess helpers)
- [ ] No hardcoded strings for error messages (use constants)
- [ ] All custom exceptions extend RuntimeException
- [ ] GlobalExceptionHandler catches ALL exception types including generic Exception
- [ ] @Slf4j used for logging — not System.out.println

---

## Response Contracts

- [ ] GET /about returns name, email, my_features (no auth required)
- [ ] GET /openapi.json returns valid JSON spec (no auth required)
- [ ] GET /search?q=keyword returns notes matching title OR content
- [ ] Pinned notes always appear first in GET /notes results
- [ ] Empty notes list returns [] not null and not 404
