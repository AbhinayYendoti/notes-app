# File Ownership Rules — Multi-Agent Boundaries

Each agent has a strictly bounded set of files it may create or modify.
**Violating these boundaries causes integration collapse.**

---

## Agent 1 — Auth Specialist

### ✅ MAY CREATE / MODIFY
```
src/main/java/com/abhinay/notesapp/
  config/SecurityConfig.java
  controller/AuthController.java
  entity/User.java
  exception/EmailAlreadyExistsException.java
  exception/InvalidTokenException.java
  filter/JwtAuthFilter.java
  dto/request/RegisterRequest.java
  dto/request/LoginRequest.java
  dto/response/AuthResponse.java
  dto/response/MessageResponse.java
  repository/UserRepository.java
  service/AuthService.java
  service/UserDetailsServiceImpl.java
  util/JwtUtil.java
src/main/resources/application.yml  (only jwt.* section)
```

### ❌ MUST NOT TOUCH
```
entity/Note.java
controller/NoteController.java
service/NoteService.java
repository/NoteRepository.java
dto/request/NoteRequest.java
dto/request/ShareRequest.java
dto/response/NoteResponse.java
ANY file under sharing/ or pinning/
```

---

## Agent 2 — Notes CRUD Specialist

### ✅ MAY CREATE / MODIFY
```
entity/Note.java
controller/NoteController.java           (CRUD endpoints only)
service/NoteService.java                 (CRUD methods only)
repository/NoteRepository.java
dto/request/NoteRequest.java
dto/response/NoteResponse.java
dto/response/PinResponse.java
exception/NoteNotFoundException.java
exception/NoteAccessDeniedException.java
```

### ❌ MUST NOT TOUCH
```
entity/User.java                         ← Agent 1 owns this
config/SecurityConfig.java               ← Agent 1 owns this
filter/JwtAuthFilter.java                ← Agent 1 owns this
util/JwtUtil.java                        ← Agent 1 owns this
controller/AuthController.java           ← Agent 1 owns this
dto/request/ShareRequest.java            ← Agent 3 owns this
```

---

## Agent 3 — Sharing + Pinning Specialist

### ✅ MAY MODIFY (add methods only — do NOT break existing code)
```
service/NoteService.java                 (ADD shareNote, togglePin — don't modify existing methods)
controller/NoteController.java           (ADD /share, /pin endpoints — don't modify existing endpoints)
```

### ✅ MAY CREATE
```
dto/request/ShareRequest.java
dto/response/PinResponse.java
exception/UserNotFoundException.java
exception/NoteAlreadySharedException.java
```

### ❌ MUST NOT TOUCH
```
entity/User.java
entity/Note.java
config/SecurityConfig.java
filter/JwtAuthFilter.java
repository/UserRepository.java           (read only — do not add methods)
dto/request/RegisterRequest.java
dto/request/LoginRequest.java
```

---

## Agent 4 — Documentation + Deployment Specialist

### ✅ MAY CREATE / MODIFY
```
config/OpenApiConfig.java
controller/AboutController.java
controller/SearchController.java
src/main/resources/application.yml      (full file — use the template from deployment_module.md)
render.yaml
Dockerfile
README.md
```

### ❌ MUST NOT TOUCH
```
ANY entity, repository, service, or filter file
config/SecurityConfig.java               ← Agent 1 owns this
ANY controller except About and Search
```

---

## Agent 5 — Validation + Reliability Specialist

### ✅ MAY CREATE (exception infrastructure — do this FIRST before all other agents)
```
exception/GlobalExceptionHandler.java
exception/NoteNotFoundException.java
exception/UserNotFoundException.java
exception/EmailAlreadyExistsException.java
exception/NoteAlreadySharedException.java
exception/NoteAccessDeniedException.java
exception/InvalidTokenException.java
dto/response/ErrorResponse.java
```

### ✅ MAY REVIEW AND ADD VALIDATION TO (but not restructure)
```
Any @Valid annotation, exception throw, or edge case gap found in other agents' files
```

### ❌ MUST NOT
```
Change method signatures in existing services or controllers
Modify entity relationships
Modify SecurityConfig or JwtAuthFilter logic
```

---

## Frontend Agent — UI Specialist

### ✅ MAY CREATE
```
notes-app-ui.html
```

### ❌ MUST NOT TOUCH
```
ANY backend Java file
pom.xml
application.yml
```

---

## Shared DTOs — Backward Compatibility Rule

> If a DTO is shared between agents (e.g. `NoteResponse`, `ErrorResponse`), its fields **may be added to but never removed or renamed** without explicit approval.

```
NoteResponse fields (FROZEN):
  UUID id
  String title
  String content
  boolean pinned
  LocalDateTime createdAt
  LocalDateTime updatedAt

ErrorResponse fields (FROZEN):
  int status
  String error
  String message
  LocalDateTime timestamp
```
