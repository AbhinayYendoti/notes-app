# Architecture Overview

## Layered Architecture (STRICT)

```
Client (HTTP)
    ↓
[SecurityFilter / JwtAuthFilter]
    ↓
[Controller Layer]        ← Receives DTOs, returns ResponseEntity
    ↓
[Service Layer]           ← Business logic, ownership checks
    ↓
[Repository Layer]        ← Spring Data JPA interfaces
    ↓
[PostgreSQL / Neon.tech]
```

## Package Structure

```
com.abhinay.notesapp/
├── config/
│   ├── SecurityConfig.java        ← Spring Security configuration
│   └── OpenApiConfig.java         ← SpringDoc configuration
├── controller/
│   ├── AuthController.java        ← /register, /login
│   ├── NoteController.java        ← /notes CRUD + share + pin
│   ├── OpenApiController.java     ← /openapi.json
│   └── AboutController.java       ← /about
├── dto/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── NoteRequest.java
│   │   └── ShareRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── NoteResponse.java
│       ├── ErrorResponse.java
│       └── MessageResponse.java
├── entity/
│   ├── User.java
│   └── Note.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── NoteNotFoundException.java
│   ├── UserNotFoundException.java
├── filter/
│   └── JwtAuthFilter.java
├── repository/
│   ├── UserRepository.java
│   └── NoteRepository.java
├── service/
│   ├── AuthService.java
│   ├── NoteService.java
│   └── UserDetailsServiceImpl.java
└── util/
    └── JwtUtil.java
```

## Key Design Decisions
- DTOs strictly separate API layer from DB layer
- Service layer validates ownership before every note operation
- JwtAuthFilter runs before every protected endpoint
- GlobalExceptionHandler catches ALL exceptions and returns structured JSON
- No field of User entity is ever serialized directly to client (use DTOs)
