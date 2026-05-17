# Authorization Matrix

Defines exactly what each user role can do on each endpoint.
Every agent must implement this matrix precisely.
Backend interviews LOVE these edge cases — get them right.

---

## Role Definitions

| Role | Description |
|---|---|
| **Anonymous** | No JWT token, or invalid/expired JWT |
| **Owner** | Authenticated user who created the note |
| **Shared User** | Authenticated user the note was explicitly shared with |
| **Other User** | Authenticated user with no relation to the note |

---

## Endpoint Authorization Matrix

| Endpoint | Anonymous | Owner | Shared User | Other User |
|---|---|---|---|---|
| `POST /register` | ✅ 201 | ✅ 201 | ✅ 201 | ✅ 201 |
| `POST /login` | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| `GET /about` | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| `GET /openapi.json` | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| `GET /notes` | ❌ 401 | ✅ 200 (own notes) | ✅ 200 (shared notes included) | ✅ 200 (own notes only) |
| `POST /notes` | ❌ 401 | ✅ 201 | ✅ 201 | ✅ 201 |
| `GET /notes/{id}` | ❌ 401 | ✅ 200 | ✅ 200 | ❌ **403** |
| `PUT /notes/{id}` | ❌ 401 | ✅ 200 | ❌ **403** | ❌ **403** |
| `DELETE /notes/{id}` | ❌ 401 | ✅ 204 | ❌ **403** | ❌ **403** |
| `POST /notes/{id}/share` | ❌ 401 | ✅ 200 | ❌ **403** | ❌ **403** |
| `PATCH /notes/{id}/pin` | ❌ 401 | ✅ 200 | ❌ **403** | ❌ **403** |
| `GET /search?q=` | ❌ 401 | ✅ 200 (own + shared) | ✅ 200 (own + shared) | ✅ 200 (own notes only) |

---

## Critical Edge Cases

### GET /notes/{id} — 403 vs 404

```
Note exists, requester is Other User:
  → 403 Forbidden   ← CORRECT (not 404)

Note does not exist, any user:
  → 404 Not Found

Why 403 not 404?
  Returning 404 for existing notes would leak whether a note ID exists.
  Always 403 when the note exists but access is denied.
```

### Shared User Permissions

```
Shared User CAN:
  ✅ GET /notes/{id}     → read the note
  ✅ GET /notes          → see it in their list

Shared User CANNOT:
  ❌ PUT /notes/{id}     → 403 (edit is owner-only)
  ❌ DELETE /notes/{id}  → 403 (delete is owner-only)
  ❌ POST /notes/{id}/share → 403 (re-sharing is owner-only)
  ❌ PATCH /notes/{id}/pin  → 403 (pinning is owner-only)
```

### Sharing Edge Cases

```
Owner shares with User A:
  ✅ User A can read

Owner shares with User A again:
  ❌ 409 "Note already shared with this user"

Owner tries to share with themselves:
  ❌ 400 "Cannot share a note with yourself"

Owner shares with non-existent email:
  ❌ 404 "User not found"

Non-owner tries to share:
  ❌ 403 "Access denied"
```

---

## Service Layer Implementation Pattern

```java
// Ownership check (for edit/delete/share/pin)
private void assertOwner(Note note, User user) {
    if (!note.getOwner().getId().equals(user.getId())) {
        throw new NoteAccessDeniedException("Access denied");
    }
}

// Read access check (for GET)
private void assertAccess(Note note, User user) {
    boolean isOwner = note.getOwner().getId().equals(user.getId());
    boolean isShared = note.getSharedWith().stream()
        .anyMatch(u -> u.getId().equals(user.getId()));
    if (!isOwner && !isShared) {
        throw new NoteAccessDeniedException("Access denied");
    }
}

// GET /notes — query returns only accessible notes
// Uses JPQL query with LEFT JOIN to include both owned and shared
```

---

## HTTP Status Code Quick Reference

| Scenario | Code | Reason |
|---|---|---|
| No/invalid JWT | 401 | Unauthenticated |
| Valid JWT, wrong user for note | 403 | Authenticated but not authorized |
| Note doesn't exist | 404 | Resource missing |
| Note exists, other user | 403 | Not 404 — prevents info leakage |
