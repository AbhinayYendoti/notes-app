# Notes App Backend API
> epiFi Engineering Internship Assignment - Abhinay Yendoti

## Live URL
> Deploy to Render and update this URL

## Tech Stack
- Java 17 + Spring Boot 3.2
- PostgreSQL (Neon.tech)
- JWT Authentication
- Spring Security 6
- Spring Data JPA
- Springdoc OpenAPI

## Setup

### 1. Clone and configure
```bash
git clone <your-repo>
cp .env.example .env
# Fill in .env with your actual values
```

### 2. Run locally
```bash
PORT=8081 ./mvnw spring-boot:run
```

For Windows PowerShell with the included `.env` file:
```powershell
$env:PORT="8081"
Get-Content .env | ForEach-Object {
  if ($_ -match '^([^#][^=]+)=(.*)$') {
    [Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), 'Process')
  }
}
.\mvnw.cmd spring-boot:run
```

### 3. Open the frontend
The static UI is available at `notes-app-ui.html`.

```powershell
start .\notes-app-ui.html
```

By default the UI calls `http://localhost:8081`. To point it somewhere else from the browser console:

```js
localStorage.setItem('notes_api_base', 'http://localhost:8080')
location.reload()
```

### 4. Deploy to Render
- Connect your GitHub repo to Render
- Add env variables from `.env`
- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -jar target/notes-app-0.0.1-SNAPSHOT.jar`

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /register | No | Register user |
| POST | /login | No | Login, get JWT |
| GET | /notes | JWT | List all notes |
| GET | /notes/{id} | JWT | Get note by ID |
| POST | /notes | JWT | Create note |
| PUT | /notes/{id} | JWT | Update note |
| DELETE | /notes/{id} | JWT | Delete note |
| POST | /notes/{id}/share | JWT | Share note |
| PATCH | /notes/{id}/pin | JWT | Pin/unpin note |
| GET | /notes/{id}/history | JWT | List owner-only note versions |
| GET | /notes/{id}/history/{versionId} | JWT | Get one owner-only version |
| POST | /notes/{id}/history/{versionId}/restore | JWT | Restore an older version |
| GET | /search?q=keyword | JWT | Search notes |
| GET | /openapi.json | No | API docs |
| GET | /about | No | About info |

## Custom Features
1. **Note Pinning** - `PATCH /notes/{id}/pin` toggles pin, pinned notes appear first.
2. **Full-Text Search** - `GET /search?q=keyword` searches title and content.
3. **Note Version History + Restore** - every `PUT /notes/{id}` stores the previous title/content as an immutable snapshot. Owners can inspect history and restore an older version; restore snapshots the current state first to prevent data loss.
4. **Pagination** - `GET /notes?page=0&size=10` optional pagination.

## Frontend Notes
- Sign in or register from `notes-app-ui.html`.
- Create a note, update it, then open the note detail page.
- Use **HISTORY** to view saved versions.
- Use **VIEW** to fetch a specific version and **RESTORE** to roll the note back.
- Shared users can still read shared notes, but history and restore actions return `403 Forbidden`.

---

## Security Notes

### Never Commit .env
The `.env` file contains real database credentials and JWT secrets.
It is listed in `.gitignore`; verify this before every `git push`.

```bash
# Run this before every push to confirm .env is not tracked
git status
# .env must NOT appear in the output
```

### Rotate Secrets If Exposed
If `.env` is ever accidentally committed:
1. Immediately rotate the Neon DB password from the Neon dashboard
2. Generate a new JWT secret
3. Update Render.com environment variables
4. Revoke all active JWTs; changing `JWT_SECRET` invalidates all existing tokens
5. Force-push a clean history or delete and recreate the repository

### Environment Variables Only
All secrets are loaded from environment variables:
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` - Neon PostgreSQL
- `JWT_SECRET` - JWT signing key, minimum 32 characters
- `JWT_EXPIRATION` - token lifetime in milliseconds, default `86400000`

Set these in your Render.com dashboard under **Environment** -> **Environment Variables**.
Never hardcode any of these values in `application.yml` or any Java file.
