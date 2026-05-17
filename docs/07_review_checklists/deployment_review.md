# Deployment Review Checklist — Pre-Submission Quality Gate

Run this checklist AFTER deploying to Render, BEFORE submitting your URL.

---

## Pre-Push (Local)

- [ ] `./mvnw clean package -DskipTests` succeeds locally
- [ ] App starts locally with real Neon DB credentials
- [ ] All Phase 1–6 curl tests pass locally
- [ ] `.env` NOT in git: run `git status` — .env must not appear
- [ ] No compile warnings or errors in build output
- [ ] Port reads from `${PORT:8080}` in application.yml — not hardcoded

---

## GitHub Repository

- [ ] Repository is public (Render free tier needs public repo OR connected account)
- [ ] `.env.example` is committed (shows key names, no real values)
- [ ] `.gitignore` includes `.env`, `target/`, `*.jar`
- [ ] `pom.xml` is committed
- [ ] `render.yaml` is committed
- [ ] `src/` directory is fully committed
- [ ] `application.yml` uses only env variable references — no hardcoded secrets

---

## Render.com Configuration

- [ ] Service type: Web Service
- [ ] Environment: Java
- [ ] Build Command: `./mvnw clean package -DskipTests`
- [ ] Start Command: `java -jar target/notes-app-0.0.1-SNAPSHOT.jar`
- [ ] All 5 env vars set in Render dashboard:
  - [ ] `DB_URL` = `<your-postgres-jdbc-url>`
  - [ ] `DB_USERNAME` = `<your-db-username>`
  - [ ] `DB_PASSWORD` = `<your-db-password>`
  - [ ] `JWT_SECRET` = `<your-32-plus-character-jwt-secret>`
  - [ ] `JWT_EXPIRATION` = `86400000`
- [ ] Deploy succeeded (green checkmark in Render dashboard)
- [ ] Logs show: "Started NotesAppApplication in X.XXX seconds"
- [ ] Logs show NO startup exceptions

---

## Live URL Verification

Replace `YOUR_URL` with your actual Render URL and run these:

```bash
BASE=https://YOUR_APP.render.com

# 1. About (no auth)
curl $BASE/about
# ✅ Expected: 200 + {"name":"Abhinay Yendoti",...}

# 2. OpenAPI (no auth)
curl $BASE/openapi.json | head -c 200
# ✅ Expected: 200 + {"openapi":"3.0.1",...}

# 3. Register
curl -X POST $BASE/register \
  -H "Content-Type: application/json" \
  -d '{"email":"epifi-test@test.com","password":"password123"}'
# ✅ Expected: 201

# 4. Login + capture token
TOKEN=$(curl -s -X POST $BASE/login \
  -H "Content-Type: application/json" \
  -d '{"email":"epifi-test@test.com","password":"password123"}' \
  | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
echo "Token: $TOKEN"
# ✅ Expected: token string starting with eyJ

# 5. Create note
NOTE=$(curl -s -X POST $BASE/notes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Note","content":"Hello from epiFi test"}')
echo $NOTE
NOTE_ID=$(echo $NOTE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
# ✅ Expected: 201 + note object with id

# 6. Get all notes
curl $BASE/notes -H "Authorization: Bearer $TOKEN"
# ✅ Expected: 200 + array with 1 note

# 7. Pin the note
curl -X PATCH $BASE/notes/$NOTE_ID/pin -H "Authorization: Bearer $TOKEN"
# ✅ Expected: 200 + {"message":"Note pinned","pinned":true}

# 8. Share note (register another user first)
curl -X POST $BASE/register \
  -H "Content-Type: application/json" \
  -d '{"email":"epifi-test2@test.com","password":"password123"}'

curl -X POST $BASE/notes/$NOTE_ID/share \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"share_with_email":"epifi-test2@test.com"}'
# ✅ Expected: 200 + {"message":"Note shared successfully"}

# 9. Delete note
curl -X DELETE $BASE/notes/$NOTE_ID -H "Authorization: Bearer $TOKEN"
# ✅ Expected: 204 (empty body)

# 10. No auth on protected endpoint
curl $BASE/notes
# ✅ Expected: 401
```

---

## Edge Case Final Checks on Live URL

```bash
# Wrong password — verify generic message
curl -X POST $BASE/login \
  -H "Content-Type: application/json" \
  -d '{"email":"epifi-test@test.com","password":"wrongpassword"}'
# ✅ Expected: 401 {"message":"Invalid email or password"}

# Invalid UUID
curl $BASE/notes/not-a-valid-uuid -H "Authorization: Bearer $TOKEN"
# ✅ Expected: 400

# Malformed JSON
curl -X POST $BASE/register \
  -H "Content-Type: application/json" \
  -d '{"email": bad json'
# ✅ Expected: 400 {"message":"Malformed JSON request body"}

# Missing field
curl -X POST $BASE/notes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Only title"}'
# ✅ Expected: 400 {"message":"Content is required"}
```

---

## Final Submission

- [ ] ALL above curl tests pass on the live URL
- [ ] Render service stays healthy after tests (check logs for errors)
- [ ] README.md has the live URL filled in
- [ ] `GET /about` returns YOUR actual email address (not placeholder)
- [ ] Submit base URL to epiFi form: `https://YOUR_APP.render.com` (no trailing slash)

---

## CodeRabbit Review (Phase 9 — Optional but Impressive)

```
1. Install CodeRabbit GitHub App on your repo
2. Open a Pull Request
3. CodeRabbit will auto-review your code
4. Fix any critical issues it flags
5. Include CodeRabbit review summary in your submission or README
   — shows you care about code quality beyond just making it work
```

---

## Loom Walkthrough (Phase 10 — Highly Recommended)

Record a 3-5 minute Loom showing:
1. The architecture diagram (docs/02_architecture/architecture.md)
2. A live curl demo of the key endpoints
3. The Render dashboard showing healthy deployment
4. One interesting edge case (e.g., 403 on unauthorized access)

Include Loom link in your submission form — epiFi explicitly values ownership and communication.
