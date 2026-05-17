# Implementation Order — Phased Execution Plan

> **CRITICAL:** Do NOT run all agents simultaneously. Follow this exact sequence.
> Each phase must be STABLE before the next phase begins.
> This is the difference between "AI-assisted coding" and "AI-orchestrated engineering."

---

## ⚠️ BIGGEST BEGINNER MISTAKE

```
❌ WRONG:
  Launch 5 agents at once → each generates 500 lines → integration hell → nothing works

✅ CORRECT:
  Phase 1 complete + verified → Phase 2 starts
  Phase 2 complete + verified → Phase 3 starts
  ...and so on
```

---

## PHASE 0 — READ BEFORE TOUCHING CODE (30 minutes)

Before any agent generates a single line of code:

```
□ Read master_prd.md completely
□ Read architecture.md completely
□ Read database_schema.md completely
□ Read api_contracts.md completely
□ Read validation_rules.md completely
□ Read error_response_contract.md completely
□ Read naming_conventions.md completely
□ Read integration_rules.md completely
□ Read file_ownership_rules.md completely

□ Confirm: Neon DB is accessible
□ Confirm: .env file is configured
□ Confirm: Java 21 + Maven installed
□ Confirm: IntelliJ or IDE is set up
```

**Do not proceed until all boxes above are checked.**

---

## PHASE 1 — Foundation (Agent 5 first, then setup)

**Goal:** Project compiles. DB connects. Tables auto-create.

### Step 1.1 — Run Agent 5 (Exceptions + Error Handler)
```
USE: docs/04_agent_prompts/agent_prompts.md → AGENT 5 prompt

Files created:
  □ exception/GlobalExceptionHandler.java
  □ exception/NoteNotFoundException.java
  □ exception/UserNotFoundException.java
  □ exception/EmailAlreadyExistsException.java
  □ exception/NoteAlreadySharedException.java
  □ exception/NoteAccessDeniedException.java
  □ exception/InvalidTokenException.java
  □ dto/response/ErrorResponse.java
```

### Step 1.2 — Project Setup
```
Files to create/verify:
  □ pom.xml (already in ZIP — verify dependencies)
  □ NotesAppApplication.java (already in ZIP)
  □ application.yml (already in ZIP — fill in env vars)
  □ .env file (copy from .env.example, fill real values)
```

### Step 1.3 — Entities + Repositories
```
Files (already in ZIP — verify they're correct):
  □ entity/User.java
  □ entity/Note.java
  □ repository/UserRepository.java
  □ repository/NoteRepository.java
```

### Phase 1 Verification ✅
```bash
./mvnw spring-boot:run
# Expected: Application starts on port 8080
# Expected: Hibernate creates tables in Neon DB
# Check Neon console: tables 'users', 'notes', 'note_shares' exist
# No compile errors, no startup exceptions
```

**DO NOT PROCEED TO PHASE 2 UNTIL PHASE 1 PASSES.**

---

## PHASE 2 — Authentication

**Goal:** Register and Login endpoints work. JWT issued and validated.

### Step 2.1 — Run Agent 1 (Auth Specialist)
```
USE: docs/04_agent_prompts/agent_prompts.md → AGENT 1 prompt

Files:
  □ config/SecurityConfig.java
  □ controller/AuthController.java
  □ dto/request/RegisterRequest.java
  □ dto/request/LoginRequest.java
  □ dto/response/AuthResponse.java
  □ dto/response/MessageResponse.java
  □ filter/JwtAuthFilter.java
  □ service/AuthService.java
  □ service/UserDetailsServiceImpl.java
  □ util/JwtUtil.java
```

### Phase 2 Verification ✅
```bash
# Test 1: Register
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123"}'
# Expected: 201 {"message":"User registered successfully"}

# Test 2: Duplicate register
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123"}'
# Expected: 409 {"status":409,"error":"Conflict","message":"Email already registered"...}

# Test 3: Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123"}'
# Expected: 200 {"access_token":"eyJ..."}

# Test 4: Wrong password
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"wrongpass"}'
# Expected: 401 {"message":"Invalid email or password"}

# Test 5: Protected endpoint without token
curl http://localhost:8080/notes
# Expected: 401
```

**DO NOT PROCEED TO PHASE 3 UNTIL ALL PHASE 2 TESTS PASS.**

---

## PHASE 3 — Notes CRUD

**Goal:** Full CRUD for notes works. Ownership enforced.

### Step 3.1 — Run Agent 2 (CRUD Specialist)
```
USE: docs/04_agent_prompts/agent_prompts.md → AGENT 2 prompt

Files:
  □ controller/NoteController.java (CRUD endpoints)
  □ service/NoteService.java (CRUD methods)
  □ dto/request/NoteRequest.java
  □ dto/response/NoteResponse.java
```

### Phase 3 Verification ✅
```bash
# Get token first
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123"}' | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

# Test 1: Create note
curl -X POST http://localhost:8080/notes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Note","content":"Hello World"}'
# Expected: 201 with note object

# Test 2: Get all notes
curl http://localhost:8080/notes -H "Authorization: Bearer $TOKEN"
# Expected: 200 + array with 1 note

# Test 3: Get by ID (save note ID from test 1)
NOTE_ID=<id from test 1>
curl http://localhost:8080/notes/$NOTE_ID -H "Authorization: Bearer $TOKEN"
# Expected: 200 + note object

# Test 4: Update note
curl -X PUT http://localhost:8080/notes/$NOTE_ID \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Title","content":"Updated Content"}'
# Expected: 200

# Test 5: Delete note
curl -X DELETE http://localhost:8080/notes/$NOTE_ID \
  -H "Authorization: Bearer $TOKEN"
# Expected: 204

# Test 6: Invalid UUID
curl http://localhost:8080/notes/not-a-uuid -H "Authorization: Bearer $TOKEN"
# Expected: 400

# Test 7: Another user can't access
# Register user2, get token2, try to access user1's note
# Expected: 403
```

**DO NOT PROCEED TO PHASE 4 UNTIL ALL PHASE 3 TESTS PASS.**

---

## PHASE 4 — Sharing + Pinning

**Goal:** Note sharing and pinning work correctly.

### Step 4.1 — Run Agent 3 (Sharing + Pinning Specialist)
```
USE: docs/04_agent_prompts/agent_prompts.md → AGENT 3 prompt

Files modified:
  □ service/NoteService.java (add shareNote, togglePin)
  □ controller/NoteController.java (add /share, /pin endpoints)

Files created:
  □ dto/request/ShareRequest.java
  □ exception/UserNotFoundException.java
  □ exception/NoteAlreadySharedException.java
```

### Phase 4 Verification ✅
```bash
# Create two users and get tokens
# Create a note as user1
# Share with user2
# Verify user2 can GET the note
# Verify user2 CANNOT PUT or DELETE
# Test self-share → 400
# Test duplicate share → 409
# Test pin toggle → 200, pinned true/false
# Test pinned notes appear first in GET /notes
```

---

## PHASE 5 — Documentation + Deployment

### Step 5.1 — Run Agent 4 (Docs + Deploy)
```
USE: docs/04_agent_prompts/agent_prompts.md → AGENT 4 prompt

Files:
  □ config/OpenApiConfig.java
  □ controller/AboutController.java
  □ controller/SearchController.java
  □ render.yaml
  □ Dockerfile
```

### Phase 5 Verification ✅
```bash
curl http://localhost:8080/about
# Expected: 200 + your name/email/features

curl http://localhost:8080/openapi.json
# Expected: 200 + valid JSON OpenAPI spec

curl "http://localhost:8080/search?q=test" -H "Authorization: Bearer $TOKEN"
# Expected: 200 + matching notes
```

---

## PHASE 6 — Validation Hardening

### Step 6.1 — Run Agent 5 again (Review pass)
```
Agent 5 reviews ALL endpoints for edge cases:
  □ All validation rules from validation_rules.md are implemented
  □ All test cases from testing_rules.md pass
  □ No stack traces leak in any response
  □ All 500 errors are caught and sanitized
```

---

## PHASE 7 — Deployment

```
□ Push code to GitHub (verify .env is in .gitignore)
□ Create Render.com web service
□ Set all env variables in Render dashboard:
    DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET, JWT_EXPIRATION
□ Deploy
□ Wait for build to succeed (~3-5 minutes)
□ Test live URL with the curl commands from Phase 2-5
□ Submit the base URL to epiFi
```

---

## PHASE 8 — Stretch Goals (if time permits)

```
□ Frontend: use docs/03_module_prds/frontend_module.md + Frontend Agent prompt
□ Docker: Dockerfile is already in the ZIP
□ Full-text search: already implemented in SearchController
□ Pagination: already implemented in NoteController
```

---

## Implementation Timeline Estimate

| Phase | Time | Notes |
|---|---|---|
| Phase 0 — Read | 30 min | Don't skip this |
| Phase 1 — Foundation | 30 min | Mostly in ZIP already |
| Phase 2 — Auth | 45 min | Run Agent 1 |
| Phase 3 — CRUD | 45 min | Run Agent 2 |
| Phase 4 — Sharing | 30 min | Run Agent 3 |
| Phase 5 — Docs | 30 min | Run Agent 4 |
| Phase 6 — Hardening | 30 min | Agent 5 review |
| Phase 7 — Deploy | 30 min | Render setup |
| **Total** | **~5 hours** | Well within 48hr deadline |
