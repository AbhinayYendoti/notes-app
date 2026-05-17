# Transaction Rules

Defines which operations require database transactions and how to implement them.
This gives senior-level engineering signal.

---

## @Transactional Placement Rule

```
✅ @Transactional belongs on SERVICE methods — NOT on controllers or repositories.
✅ Read-only operations use @Transactional(readOnly = true) for performance.
✅ Write operations use @Transactional (default — read-write).
❌ Never put @Transactional on controller methods.
❌ Never put @Transactional on repository methods (Spring Data handles this).
```

---

## Transaction Map

| Service Method | Annotation | Reason |
|---|---|---|
| `register()` | `@Transactional` | Inserts new User row |
| `login()` | None (read only, no @Transactional needed) | Only reads — no write |
| `getAllNotes()` | `@Transactional(readOnly = true)` | Read-only query + lazy loading |
| `getNoteById()` | `@Transactional(readOnly = true)` | Read-only + lazy loads sharedWith |
| `createNote()` | `@Transactional` | Inserts new Note row |
| `updateNote()` | `@Transactional` | Updates Note fields |
| `deleteNote()` | `@Transactional` | Deletes Note + cascades to note_shares |
| `shareNote()` | `@Transactional` | Inserts row into note_shares join table |
| `togglePin()` | `@Transactional` | Updates Note.pinned field |
| `searchNotes()` | `@Transactional(readOnly = true)` | Read-only JPQL query |

---

## Lazy Loading Problem — How to Avoid

`Note.sharedWith` is `FetchType.LAZY`. Accessing it outside a transaction causes `LazyInitializationException`.

```java
// ✅ SAFE — inside @Transactional service method
@Transactional(readOnly = true)
public NoteResponse getNoteById(UUID id, Authentication auth) {
    Note note = getNoteOrThrow(id);
    assertAccess(note, user);        // accesses note.sharedWith — safe inside @Transactional
    return NoteResponse.from(note);  // from() only reads basic fields — no lazy access
}

// ❌ UNSAFE — accessing lazy collection in controller (outside transaction)
// Never do: note.getSharedWith().size() in a controller method
```

**Solution:** All business logic that accesses `sharedWith` stays in the service layer under `@Transactional`.

---

## Delete Cascade Behaviour

When a Note is deleted:
```
notes row deleted
  → note_shares rows cascade-deleted automatically (ON DELETE CASCADE in schema)
  → User rows NOT affected (users persist after their notes are deleted)
```

JPA mapping that enables cascade:
```java
// In Note entity — no explicit CascadeType needed for note_shares
// The DB-level ON DELETE CASCADE handles the join table cleanup
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "note_shares", ...)
private Set<User> sharedWith;
```

---

## Transaction Rollback Rules

Spring's `@Transactional` rolls back on:
- Any `RuntimeException` (unchecked) — including all custom domain exceptions
- Any `Error`

Spring does NOT roll back on:
- Checked exceptions (unless `rollbackFor` specified)

Since all custom exceptions extend `RuntimeException`, all writes are safe — any exception mid-operation rolls back the entire transaction.

```java
// Example: shareNote — atomic operation
@Transactional
public MessageResponse shareNote(...) {
    Note note = getNoteOrThrow(noteId);    // may throw NoteNotFoundException → rollback
    assertOwner(note, owner);              // may throw NoteAccessDeniedException → rollback
    // ... validation
    note.getSharedWith().add(target);
    noteRepository.save(note);             // if this fails → entire transaction rolls back
    return new MessageResponse("Note shared successfully");
}
```

---

## Optimistic Locking (Optional — Extra Credit)

For concurrent note updates, add `@Version` to Note entity:
```java
@Version
private Long version;  // JPA auto-increments; throws OptimisticLockException on conflict
```

Handle `OptimisticLockException` in GlobalExceptionHandler → 409 "Note was modified by another request, please retry."
