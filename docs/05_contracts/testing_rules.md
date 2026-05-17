# Testing Rules & Required Test Cases

## Testing Philosophy

The assignment says: **"You'll be judged for edge case handling."**
This means automated tests will probe every edge case. These tests must PASS.

---

## Required Edge Cases (Automated Test Targets)

### Auth Tests
| Test Case | Expected |
|---|---|
| Register with valid email + password | 201 + success message |
| Register with duplicate email | 409 + "Email already registered" |
| Register with invalid email format | 400 + "Invalid email format" |
| Register with password < 8 chars | 400 + "Password must be at least 8 characters" |
| Register with blank email | 400 |
| Register with blank password | 400 |
| Register with whitespace-only email | 400 |
| Login with correct credentials | 200 + access_token |
| Login with wrong password | 401 + "Invalid email or password" |
| Login with non-existent email | 401 + "Invalid email or password" (SAME MESSAGE) |
| Login with blank email | 400 |
| Login with malformed JSON | 400 |

### JWT / Auth Header Tests
| Test Case | Expected |
|---|---|
| GET /notes with no Authorization header | 401 |
| GET /notes with "Bearer " (empty token) | 401 |
| GET /notes with invalid JWT | 401 |
| GET /notes with expired JWT | 401 |
| GET /notes with valid JWT | 200 |
| GET /notes with "Token abc" (not Bearer) | 401 |

### Notes CRUD Tests
| Test Case | Expected |
|---|---|
| Create note with valid data | 201 + note object |
| Create note with blank title | 400 |
| Create note with whitespace-only title | 400 |
| Create note with title > 255 chars | 400 |
| Create note with blank content | 400 |
| Create note with content > 50000 chars | 400 |
| Get all notes (empty) | 200 + [] |
| Get all notes (with notes) | 200 + array |
| Get note by valid ID (owned) | 200 + note |
| Get note by non-existent ID | 404 |
| Get note by invalid UUID string | 400 |
| Get note owned by another user | 403 |
| Update note (owner) | 200 + updated note |
| Update note (not owner) | 403 |
| Delete note (owner) | 204 |
| Delete note (not owner) | 403 |
| Delete non-existent note | 404 |

### Sharing Tests
| Test Case | Expected |
|---|---|
| Share note with valid email (other user) | 200 + success message |
| Share note with non-existent email | 404 + "User not found" |
| Share note with yourself | 400 + "Cannot share a note with yourself" |
| Share note already shared with same user | 409 + "Note already shared with this user" |
| Share note you don't own | 403 |
| Share with blank email | 400 |
| Share with invalid email format | 400 |
| Shared user reads note via GET /notes/{id} | 200 |
| Shared user tries PUT /notes/{id} | 403 |
| Shared user tries DELETE /notes/{id} | 403 |

### Pinning Tests
| Test Case | Expected |
|---|---|
| Pin note (owner) | 200 + { pinned: true } |
| Unpin pinned note | 200 + { pinned: false } |
| Pin note you don't own | 403 |
| Pinned notes appear first in GET /notes | 200, pinned items at index 0 |

### Pagination Tests
| Test Case | Expected |
|---|---|
| GET /notes?page=0&size=5 | 200 + paginated response |
| GET /notes?page=-1 | 400 |
| GET /notes?size=0 | 400 |
| GET /notes?size=101 | 400 |
| GET /notes (no params) | 200 + flat array |

### OpenAPI / About Tests
| Test Case | Expected |
|---|---|
| GET /about (no auth) | 200 + name/email/features |
| GET /openapi.json (no auth) | 200 + valid JSON spec |
| GET /search?q=keyword (with auth) | 200 + matching notes |

---

## Manual Verification Checklist

Before deploying, verify manually with curl or Postman:

```bash
# 1. Register
curl -X POST https://your-app.render.com/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
# Expected: 201

# 2. Login
curl -X POST https://your-app.render.com/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
# Expected: 200 + {"access_token":"..."}
# Save token as TOKEN=...

# 3. Create note
curl -X POST https://your-app.render.com/notes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"My First Note","content":"Hello World"}'
# Expected: 201

# 4. Get all notes
curl https://your-app.render.com/notes \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 + array

# 5. About
curl https://your-app.render.com/about
# Expected: 200 + your info

# 6. OpenAPI
curl https://your-app.render.com/openapi.json
# Expected: 200 + OpenAPI 3.0 JSON
```
