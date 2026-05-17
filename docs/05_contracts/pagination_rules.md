# Pagination Rules

## Endpoint
```
GET /notes?page=0&size=10
```

Both parameters are **optional**. If omitted, return all notes (non-paginated list).
If either parameter is provided, return a paginated response.

---

## Request Parameters

| Parameter | Type | Default | Min | Max | Notes |
|---|---|---|---|---|---|
| `page` | Integer | 0 | 0 | — | 0-indexed. Negative values → 400 |
| `size` | Integer | 10 | 1 | 100 | Values > 100 → 400. Values < 1 → 400 |

---

## Validation Rules

```
page < 0         → 400 "Page number must be 0 or greater"
size < 1         → 400 "Page size must be at least 1"
size > 100       → 400 "Page size must not exceed 100"
page = non-int   → 400 "Invalid page parameter"
size = non-int   → 400 "Invalid size parameter"
```

---

## Paginated Response Format

When `page` or `size` is present, return Spring's `Page<NoteResponse>` serialized:

```json
{
  "content": [
    {
      "id": "uuid",
      "title": "string",
      "content": "string",
      "pinned": false,
      "createdAt": "2026-01-01T10:00:00",
      "updatedAt": "2026-01-01T10:00:00"
    }
  ],
  "totalElements": 42,
  "totalPages": 5,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false
}
```

---

## Non-Paginated Response (no params)

```json
[
  {
    "id": "uuid",
    "title": "string",
    "content": "string",
    "pinned": false,
    "createdAt": "2026-01-01T10:00:00",
    "updatedAt": "2026-01-01T10:00:00"
  }
]
```

---

## Ordering (ALWAYS enforced)

Pinned notes appear FIRST, then sorted by `createdAt DESC`:
```sql
ORDER BY pinned DESC, created_at DESC
```

This applies to both paginated and non-paginated responses.

---

## Controller Implementation Pattern

```java
@GetMapping
public ResponseEntity<?> getAllNotes(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        Authentication auth) {

    // Validate pagination params if provided
    if (page != null && page < 0)
        throw new BadRequestException("Page number must be 0 or greater");
    if (size != null && size < 1)
        throw new BadRequestException("Page size must be at least 1");
    if (size != null && size > 100)
        throw new BadRequestException("Page size must not exceed 100");

    if (page != null || size != null) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        return ResponseEntity.ok(noteService.getAllNotesPaged(auth, PageRequest.of(p, s)));
    }

    return ResponseEntity.ok(noteService.getAllNotes(auth));
}
```
