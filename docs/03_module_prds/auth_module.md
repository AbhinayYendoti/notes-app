# Auth Module PRD — Agent 1

## Scope
ONLY implement: UserEntity, UserRepository, RegisterRequest, LoginRequest, AuthResponse, AuthService, AuthController, JwtUtil, UserDetailsServiceImpl, SecurityConfig, JwtAuthFilter.

DO NOT touch: Note entity, NoteController, NoteService, sharing logic.

---

## GLOBAL ENGINEERING RULES APPLY (see master_prd.md)

---

## Files to Create

### 1. entity/User.java
```
Fields: UUID id, String email, String password, LocalDateTime createdAt
Annotations: @Entity, @Table(name="users"), @Id, @GeneratedValue
id: UUID auto-generated
email: @Column(unique=true, nullable=false)
password: stored as BCrypt hash ONLY
```

### 2. repository/UserRepository.java
```
extends JpaRepository<User, UUID>
Methods:
  Optional<User> findByEmail(String email)
  boolean existsByEmail(String email)
```

### 3. dto/request/RegisterRequest.java
```
Fields:
  @NotBlank(message="Email is required")
  @Email(message="Invalid email format")
  @Size(max=255, message="Email too long")
  String email

  @NotBlank(message="Password is required")
  @Size(min=8, message="Password must be at least 8 characters")
  @Size(max=100, message="Password too long")
  String password
```

### 4. dto/request/LoginRequest.java
```
Fields:
  @NotBlank String email
  @NotBlank String password
```

### 5. dto/response/AuthResponse.java
```
Fields: String accessToken
Constructor: AuthResponse(String accessToken)
```

### 6. util/JwtUtil.java (MAX 150 lines)
```
Uses: io.jsonwebtoken (jjwt 0.12.x)
Methods:
  String generateToken(String email)
  String extractEmail(String token)
  boolean isTokenValid(String token)
  boolean isTokenExpired(String token)
  
Secret from: env JWT_SECRET (via @Value)
Expiration from: env JWT_EXPIRATION (milliseconds)
Handle: ExpiredJwtException, MalformedJwtException, SignatureException → throw custom exceptions
```

### 7. service/UserDetailsServiceImpl.java
```
implements UserDetailsService
loadUserByUsername(email): find user by email, return UserDetails
throw UsernameNotFoundException if not found
```

### 8. filter/JwtAuthFilter.java (MAX 100 lines)
```
extends OncePerRequestFilter
- Extract Bearer token from Authorization header
- Validate token with JwtUtil
- Set SecurityContext if valid
- On any failure: clear context, do NOT throw → let Spring Security return 401
```

### 9. service/AuthService.java (MAX 150 lines)
```
register(RegisterRequest):
  - Trim email, lowercase it
  - Check if email exists → throw EmailAlreadyExistsException (409)
  - Hash password with BCryptPasswordEncoder
  - Save user
  - Return success message

login(LoginRequest):
  - Find user by email → if not found, throw 401 (GENERIC message only)
  - Verify password with BCrypt → if wrong, throw 401 (SAME generic message)
  - Generate JWT token
  - Return AuthResponse
```

### 10. controller/AuthController.java (MAX 100 lines)
```
@RestController
POST /register → 201 + MessageResponse
POST /login → 200 + AuthResponse
Use @Valid on all request bodies
```

### 11. config/SecurityConfig.java (MAX 120 lines)
```
Permit: /register, /login, /about, /openapi.json, /swagger-ui/**
Protect: all other endpoints (require JWT)
Disable CSRF (REST API)
Stateless session management
Add JwtAuthFilter before UsernamePasswordAuthenticationFilter
CORS: allow all origins (for automated testing)
```

---

## Edge Cases to Handle
- Email with extra spaces → trim before save/lookup
- Register with existing email → 409 Conflict
- Login with wrong password → 401 (same message as wrong email)
- Login with non-existent email → 401 (same message)
- Missing Authorization header → 401
- Expired JWT → 401
- JWT for deleted user → 401
- Malformed JWT → 401
