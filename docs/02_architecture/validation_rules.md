# Validation Rules — Edge Case Contract

This file defines ALL validations. Every agent must implement these.

## Auth Validations

### POST /register
| Field | Rule | Error |
|---|---|---|
| email | Not null, not blank | "Email is required" |
| email | Valid format (regex) | "Invalid email format" |
| email | Max 255 chars | "Email too long" |
| email | Unique in DB | "Email already registered" (409) |
| password | Not null, not blank | "Password is required" |
| password | Min 8 chars | "Password must be at least 8 characters" |
| password | Max 100 chars | "Password too long" |

### POST /login
| Field | Rule | Error |
|---|---|---|
| email | Not null, not blank | 401 "Invalid email or password" |
| password | Not null, not blank | 401 "Invalid email or password" |
| email+password | Must match DB | 401 "Invalid email or password" |

**IMPORTANT**: Login NEVER reveals whether email exists. Always return generic 401.

## Notes Validations

### POST /notes, PUT /notes/{id}
| Field | Rule | Error |
|---|---|---|
| title | Not null, not blank | "Title is required" |
| title | Max 255 chars | "Title is too long (max 255)" |
| content | Not null, not blank | "Content is required" |
| content | Max 50000 chars | "Content is too long (max 50000)" |
| id (path) | Valid UUID format | 400 "Invalid note ID" |

### GET/PUT/DELETE /notes/{id}
| Check | Rule | Error |
|---|---|---|
| Note exists | Must exist in DB | 404 "Note not found" |
| Ownership | Must be owner | 403 "Access denied" |
| JWT | Must be valid | 401 "Unauthorized" |

### POST /notes/{id}/share
| Field | Rule | Error |
|---|---|---|
| share_with_email | Not null, not blank | "Email is required" |
| share_with_email | Valid email format | "Invalid email format" |
| share_with_email | User must exist | 404 "User not found" |
| share_with_email | Cannot share with self | 400 "Cannot share a note with yourself" |
| share_with_email | Not already shared | 409 "Note already shared with this user" |
| Note ownership | Must be owner | 403 "Access denied" |

## JWT Validations
| Check | Error |
|---|---|
| Missing Authorization header | 401 |
| Header not "Bearer xxx" format | 401 |
| Expired token | 401 "Token expired" |
| Invalid signature | 401 "Invalid token" |
| Malformed token | 401 "Invalid token" |
| User from token not in DB | 401 |

## Pagination (Stretch)
| Param | Rule |
|---|---|
| page | Default 0, min 0 |
| size | Default 10, min 1, max 100 |
| Invalid values | Return 400 |

## General
- Whitespace-only strings are treated as blank (use @NotBlank)
- Trim all string inputs before processing
- Malformed JSON body → 400 with message
- Unknown fields in JSON → ignored silently
- Content-Type must be application/json for POST/PUT → 415 if not
