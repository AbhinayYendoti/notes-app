# API Contracts

All responses follow consistent structure.

## Success Responses

### POST /register → 201
```json
{ "message": "User registered successfully" }
```

### POST /login → 200
```json
{ "access_token": "eyJhbGci..." }
```

### GET /notes → 200
```json
[{
  "id": "uuid",
  "title": "string",
  "content": "string",
  "pinned": false,
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}]
```

### POST /notes → 201
```json
{
  "id": "uuid",
  "title": "string",
  "content": "string",
  "pinned": false,
  "createdAt": "2025-01-01T00:00:00",
  "updatedAt": "2025-01-01T00:00:00"
}
```

### DELETE /notes/{id} → 204 (no body)

### POST /notes/{id}/share → 200
```json
{ "message": "Note shared successfully" }
```

### PATCH /notes/{id}/pin → 200
```json
{ "message": "Note pinned/unpinned successfully", "pinned": true }
```

## Error Responses (ALL errors)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Descriptive message here",
  "timestamp": "2025-01-01T00:00:00"
}
```

## HTTP Status Codes
| Scenario | Code |
|---|---|
| Created | 201 |
| Success | 200 |
| No content | 204 |
| Bad input | 400 |
| Unauthorized (no/bad JWT) | 401 |
| Forbidden (not your resource) | 403 |
| Not found | 404 |
| Duplicate | 409 |
| Server error | 500 |
