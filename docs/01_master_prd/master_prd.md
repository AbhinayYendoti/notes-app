# 📝 Notes App — Master PRD
> **Abhinay Yendoti | epiFi Engineering Internship Assignment**

---

## Project Overview

Build a production-grade multi-user notes backend service (think: Google Keep API).
Expose REST APIs for user management, notes CRUD, sharing, and documentation.
Deploy on Render.com (free tier). Automated tests will hit the live URL.

---

## Core Requirements Summary

| # | Endpoint | Auth |
|---|---|---|
| 1 | POST /register | No |
| 2 | POST /login | No |
| 3 | GET /notes | JWT |
| 4 | GET /notes/{id} | JWT |
| 5 | POST /notes | JWT |
| 6 | PUT /notes/{id} | JWT |
| 7 | DELETE /notes/{id} | JWT |
| 8 | POST /notes/{id}/share | JWT |
| 9 | GET /openapi.json | No |
| 10 | GET /about | No |
| 11 | GET /search?q=keyword | JWT (stretch) |

---

## Custom Feature (Required)

**Note Pinning** — Users can pin important notes. Pinned notes always appear first in GET /notes results.
- `PATCH /notes/{id}/pin` → pins/unpins a note
- `pinned` field in all note responses
- Why: Mirrors real-world note apps (Google Keep, Apple Notes), demonstrating product sense.

---

## Stretch Goals

- [ ] Paginate GET /notes (page, size query params)
- [ ] Full-text search: GET /search?q=keyword
- [ ] Dockerize
- [ ] Basic frontend

---

## Tech Stack (MANDATORY)

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Database | PostgreSQL (Neon.tech hosted) |
| Auth | JWT (jjwt library) |
| Security | Spring Security 6 |
| ORM | Spring Data JPA + Hibernate |
| Docs | SpringDoc OpenAPI 3 |
| Deploy | Render.com |

---

## Environment Variables (.env)

```
DB_URL=<your-postgres-jdbc-url>
DB_USERNAME=<your-db-username>
DB_PASSWORD=<your-db-password>
JWT_SECRET=<your-32-plus-character-jwt-secret>
JWT_EXPIRATION=86400000
```

---

## Global Engineering Rules (MUST be followed in EVERY module)

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
STRICT ENGINEERING RULES — APPLY EVERYWHERE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

FILE SIZE:
  - MAX 250 lines per file. Split if larger.
  - Prefer small, focused classes.

ARCHITECTURE:
  - Strict layered: Controller → Service → Repository
  - DTOs for ALL request/response (never expose Entity directly)
  - Global exception handler for ALL errors
  - Proper HTTP status codes always

VALIDATION:
  - @Valid + custom validators on ALL inputs
  - Validate: nulls, blanks, format, length, type
  - Never trust input — validate defensively
  - Edge cases: empty strings, whitespace-only, SQL injection chars, oversized payloads

SECURITY:
  - JWT required on all /notes endpoints
  - Users NEVER access other users' data
  - Passwords hashed with BCrypt always
  - Never expose stack traces or internal messages
  - Validate ownership on every note operation

API STANDARDS:
  - Consistent JSON response structure
  - Proper Content-Type headers
  - CORS configured for deployed URL
  - No 500 errors leaked to client

DEPLOYMENT:
  - All secrets via environment variables
  - NEVER hardcode credentials
  - .env in .gitignore
  - App listens on PORT env variable (Render requirement)

CODE QUALITY:
  - No God classes
  - Single responsibility per class
  - Meaningful names
  - Production-ready, interview-defensible
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## Module Breakdown

| Module | PRD File | Agent |
|---|---|---|
| Auth (Register/Login/JWT) | `03_module_prds/auth_module.md` | Agent 1 |
| Notes CRUD | `03_module_prds/notes_crud_module.md` | Agent 2 |
| Note Sharing | `03_module_prds/sharing_module.md` | Agent 3 |
| Note Pinning (Custom) | `03_module_prds/pinning_module.md` | Agent 3 |
| Validation + Edge Cases | `03_module_prds/validation_module.md` | Agent 5 |
| Exception Handling | `03_module_prds/exception_handling_module.md` | Agent 5 |
| OpenAPI + About | `03_module_prds/openapi_module.md` | Agent 4 |
| Deployment | `03_module_prds/deployment_module.md` | Agent 4 |
