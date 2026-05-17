# Sharing + Pinning Module PRD — Agent 3

## Scope
ONLY implement: share endpoint logic, pin toggle logic. Add to NoteService and NoteController.
Read Note entity and User entity structure but DO NOT modify them.
DO NOT touch: auth, basic CRUD, OpenAPI.

---

## GLOBAL ENGINEERING RULES APPLY (see master_prd.md)

---

## Add to NoteService.java

### shareNote(UUID noteId, ShareRequest request, Authentication auth)
```
Steps:
1. Get current user (owner)
2. Find note by id → 404 if not found
3. Current user is NOT note.owner → 403 "Access denied"
4. Trim + validate share_with_email (not blank, valid format)
5. Find target user by email → 404 "User not found"
6. Target user == current user → 400 "Cannot share a note with yourself"
7. Note already shared with target → 409 "Note already shared with this user"
8. Add target to note.sharedWith
9. Save note
10. Return 200 { "message": "Note shared successfully" }
```

### togglePin(UUID noteId, Authentication auth)
```
Steps:
1. Get current user
2. Find note by id → 404 if not found
3. Current user is NOT note.owner → 403 "Access denied"
4. Toggle note.pinned (true → false, false → true)
5. Save note
6. Return 200 { "message": "Note pinned/unpinned", "pinned": <new value> }
```

## Add to NoteController.java

```
POST /notes/{id}/share → 200 MessageResponse
PATCH /notes/{id}/pin → 200 (pinned status message)
```

## dto/request/ShareRequest.java
```
@NotBlank(message="Email is required")
@Email(message="Invalid email format")
@Size(max=255)
String shareWithEmail
```

## dto/response/PinResponse.java
```
String message
boolean pinned
```

---

## Edge Cases
- Share with non-registered email → 404 "User not found"
- Share with self → 400 "Cannot share a note with yourself"
- Share already-shared note → 409 "Note already shared with this user"
- Share note you don't own → 403
- Pin note you don't own → 403
- Pin non-existent note → 404
- Blank email in share request → 400
- Invalid email format → 400
