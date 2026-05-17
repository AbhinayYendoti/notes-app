# Notes App Backend API

Multi-user notes backend built for the epiFi Engineering Internship Assignment.

The API supports JWT authentication, note CRUD, sharing, pinning, search, version history, restore, validation, and secure ownership checks.

## Live Deployment

Base URL:

```text
https://notes-app-backend-xvbh.onrender.com
```

Public endpoints:

```text
https://notes-app-backend-xvbh.onrender.com/about
https://notes-app-backend-xvbh.onrender.com/openapi.json
```

Submit this base URL for automated testing:

```text
https://notes-app-backend-xvbh.onrender.com
```

## About

```http
GET /about
```

Returns assignment metadata in the required format:

```json
{
  "name": "Abhinay Yendoti",
  "email": "abhinayyendoti@gmail.com",
  "my features": {
    "Feature Name": "Feature description. Why it was chosen."
  }
}
```

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Security 6
- Spring Data JPA
- PostgreSQL
- JWT authentication with JJWT
- BCrypt password hashing
- Springdoc OpenAPI
- Docker
- Render Web Service
- Render Postgres

## Core Features

### Authentication

- User registration with email/password validation
- Login with JWT access token
- Passwords stored using BCrypt
- Protected routes require `Authorization: Bearer <token>`
- Generic login errors to avoid user enumeration

### Notes CRUD

- Create, read, update, and delete notes
- Notes are scoped to authenticated users
- UUID-based note IDs
- Validation for blank/missing title and content
- Clean JSON error responses for invalid requests

### Sharing

- Note owners can share notes with another registered user
- Shared users can read the note
- Shared users cannot update, delete, restore, or inspect version history
- Duplicate shares, self-shares, and unknown users are handled explicitly

### Note Pinning

- `PATCH /notes/{id}/pin`
- Toggles the note's pinned state
- Pinned notes are returned first
- Chosen because it mirrors real note-taking products and improves usability

### Full-Text Search

- `GET /search?q=keyword`
- Searches note title and content
- Returns only notes the authenticated user owns or can access
- Chosen because search is essential once note count grows

### Version History and Restore

- Every note update stores the previous title/content as an immutable version
- Owners can inspect version history
- Owners can restore an older version
- Restore snapshots the current state first to prevent data loss
- Chosen to demonstrate transaction safety, auditability, and rollback behavior

### Pagination

- `GET /notes?page=0&size=10`
- Optional pagination support for scalable note listing

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/register` | No | Register a new user |
| `POST` | `/login` | No | Login and receive JWT |
| `GET` | `/about` | No | Assignment metadata |
| `GET` | `/openapi.json` | No | OpenAPI specification |
| `GET` | `/notes` | JWT | List accessible notes |
| `POST` | `/notes` | JWT | Create a note |
| `GET` | `/notes/{id}` | JWT | Get note by ID |
| `PUT` | `/notes/{id}` | JWT | Update owned note |
| `DELETE` | `/notes/{id}` | JWT | Delete owned note |
| `POST` | `/notes/{id}/share` | JWT | Share owned note |
| `PATCH` | `/notes/{id}/pin` | JWT | Toggle pin state |
| `GET` | `/search?q=keyword` | JWT | Search accessible notes |
| `GET` | `/notes/{id}/history` | JWT | List owner-only note versions |
| `GET` | `/notes/{id}/history/{versionId}` | JWT | Get a specific owner-only version |
| `POST` | `/notes/{id}/history/{versionId}/restore` | JWT | Restore an older version |

## Request Examples

### Register

```bash
curl -X POST https://notes-app-backend-xvbh.onrender.com/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### Login

```bash
curl -X POST https://notes-app-backend-xvbh.onrender.com/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### Create Note

```bash
curl -X POST https://notes-app-backend-xvbh.onrender.com/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"Deployment Notes","content":"Render deployment is live."}'
```

### Search Notes

```bash
curl "https://notes-app-backend-xvbh.onrender.com/search?q=deployment" \
  -H "Authorization: Bearer <token>"
```

## Local Setup

### 1. Clone

```bash
git clone <your-repo-url>
cd notes-app
```

### 2. Configure Environment

Create a `.env` file from the example:

```bash
cp .env.example .env
```

Required variables:

```env
DB_URL=jdbc:postgresql://<host>:5432/<database>
DB_USERNAME=<database_user>
DB_PASSWORD=<database_password>
JWT_SECRET=<minimum_32_character_secret>
JWT_EXPIRATION=86400000
```

### 3. Run Locally

Linux/macOS:

```bash
PORT=8081 ./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
$env:PORT="8081"
Get-Content .env | ForEach-Object {
  if ($_ -match '^([^#][^=]+)=(.*)$') {
    [Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), 'Process')
  }
}
.\mvnw.cmd spring-boot:run
```

Local base URL:

```text
http://localhost:8081
```

## Frontend

A lightweight static UI is included:

```text
notes-app-ui.html
```

Open it directly in the browser.

To point the UI to the deployed backend, run this in the browser console:

```js
localStorage.setItem('notes_api_base', 'https://notes-app-backend-xvbh.onrender.com')
location.reload()
```

To point it back to local development:

```js
localStorage.setItem('notes_api_base', 'http://localhost:8081')
location.reload()
```

## Testing

The repository includes an end-to-end API test script:

```text
api_test.py
```

Run against the deployed API:

```powershell
$env:BASE="https://notes-app-backend-xvbh.onrender.com"
python api_test.py
```

Expected result:

```text
PASSED: 65  |  FAILED: 0  |  TOTAL: 65
ALL PASSED
```

The test suite covers:

- Public `/about` and `/openapi.json`
- Registration and login
- JWT-protected routes
- Notes CRUD
- Input validation
- Unauthorized and forbidden access
- Sharing rules
- Pin/unpin behavior
- Search
- Version history
- Restore behavior
- Delete behavior

## Deployment

This app is deployed on Render using Docker.

Render detected the included `Dockerfile` and builds the app with a multi-stage image:

1. Build stage uses Java 17 JDK and Maven wrapper
2. Runtime stage uses Java 17 JRE
3. The app starts with `java -jar app.jar`

Required Render environment variables:

```env
DB_URL=jdbc:postgresql://<render-internal-host>:5432/<database>
DB_USERNAME=<database_user>
DB_PASSWORD=<database_password>
JWT_SECRET=<minimum_32_character_secret>
JWT_EXPIRATION=86400000
```

Current deployment uses:

- Render Web Service
- Render Postgres
- Docker runtime
- Oregon region for both service and database

## Docker

Build locally:

```bash
docker build -t notes-app .
```

Run locally:

```bash
docker run --rm -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://<host>:5432/<database>" \
  -e DB_USERNAME="<database_user>" \
  -e DB_PASSWORD="<database_password>" \
  -e JWT_SECRET="<minimum_32_character_secret>" \
  -e JWT_EXPIRATION="86400000" \
  notes-app
```

## Security Notes

- `.env` is ignored by Git and must never be committed
- JWT secret is loaded only from environment variables
- Database credentials are loaded only from environment variables
- Passwords are hashed with BCrypt
- Stateless session management is used
- Protected routes require JWT
- Users cannot access notes they neither own nor have been shared on
- Shared users have read-only access
- Global exception handling returns consistent JSON errors

Before every push:

```bash
git status
```

Confirm `.env` is not staged or tracked.

## Project Structure

```text
src/main/java/com/abhinay/notesapp
  config/        Security, Jackson, OpenAPI configuration
  controller/    REST controllers
  dto/           Request and response DTOs
  entity/        JPA entities
  exception/     Custom exceptions and global handler
  filter/        JWT authentication filter
  repository/    Spring Data repositories
  service/       Business logic
  util/          JWT utility

src/main/resources
  application.yml

docs/
  Architecture, contracts, PRDs, review checklists
```

## Author

Abhinay Yendoti

```text
abhinayyendoti@gmail.com
```
