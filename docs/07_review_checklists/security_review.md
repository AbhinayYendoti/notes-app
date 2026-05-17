# Security Review Checklist — Pre-Submission Quality Gate

---

## JWT Security

- [ ] JWT secret loaded from env variable `${jwt.secret}` — NEVER hardcoded
- [ ] JWT expiration loaded from env variable `${jwt.expiration}`
- [ ] JWT signed with HMAC-SHA256 or stronger (jjwt 0.12.x default)
- [ ] JwtUtil.parseClaims() catches ExpiredJwtException → throws InvalidTokenException
- [ ] JwtUtil.parseClaims() catches MalformedJwtException → throws InvalidTokenException
- [ ] JwtUtil.parseClaims() catches SignatureException → throws InvalidTokenException
- [ ] JwtAuthFilter: on ANY token parsing failure → clears SecurityContext, does NOT throw
- [ ] JwtAuthFilter: does NOT throw exception (would cause 500) — silently clears context
- [ ] Token with non-existent user (deleted user) → 401 (UserDetailsService throws, filter clears)

---

## Password Security

- [ ] Passwords hashed with BCryptPasswordEncoder (strength ≥ 10, Spring default)
- [ ] Raw password NEVER logged
- [ ] Raw password NEVER stored in DB
- [ ] Raw password NEVER included in any response
- [ ] Password comparison done with passwordEncoder.matches() — NEVER string equality
- [ ] Register endpoint: password accepted, immediately hashed, original discarded

---

## Authorization Security

- [ ] SecurityConfig sets SessionCreationPolicy.STATELESS
- [ ] SecurityConfig disables CSRF (correct for stateless JWT API)
- [ ] All /notes/** endpoints require authentication in SecurityConfig
- [ ] /register, /login, /about, /openapi.json explicitly permitted without auth
- [ ] CORS configured to allow all origins (required for automated test runner)
- [ ] No Spring Security default login page exposed
- [ ] Method-level authorization: ownership checked in SERVICE layer, not just controller

---

## Error Message Security

- [ ] Login never reveals whether email exists — same message for wrong email AND wrong password
- [ ] 500 responses NEVER contain stack traces, exception class names, or DB error details
- [ ] 500 responses always return sanitized: `{"message": "Internal server error"}`
- [ ] GlobalExceptionHandler catches generic Exception as final fallback
- [ ] @Slf4j logs actual exception server-side for debugging (not lost)
- [ ] DB constraint violations (duplicate key etc.) caught and converted to 409 — not 500

---

## Environment & Secrets

- [ ] `.env` file is in `.gitignore` — verify with `git status` before first push
- [ ] `.env` is NOT committed to GitHub — check `git log` if uncertain
- [ ] `application.yml` uses `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}` — no hardcoded values
- [ ] Render.com env vars match exactly: DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET, JWT_EXPIRATION
- [ ] `.env.example` committed to GitHub (no real values, just key names)
- [ ] JWT_SECRET is at least 32 characters

---

## Input Security

- [ ] All string inputs trimmed before use (prevents whitespace bypass)
- [ ] Email inputs lowercased before DB lookup (prevents case-sensitivity bypass)
- [ ] Content-Type: application/json enforced on POST/PUT (415 if wrong)
- [ ] UUID path parameters validated — MethodArgumentTypeMismatchException caught → 400
- [ ] No SQL injection possible (Spring Data JPA uses parameterized queries always)
- [ ] No direct entity exposure (DTOs always used in responses)
