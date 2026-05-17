# OpenAPI Contract Rules

## Endpoint: GET /openapi.json

Returns a valid OpenAPI 3.0 specification. No auth required.
Automated tests will validate this endpoint exists and returns valid JSON.

---

## SpringDoc Configuration (application.yml)

```yaml
springdoc:
  api-docs:
    path: /openapi.json     # EXACT path — do not change
  swagger-ui:
    enabled: false          # Disable UI, only expose JSON spec
```

---

## Every Endpoint Must Be Documented

```
POST /register
POST /login
GET  /notes
GET  /notes/{id}
POST /notes
PUT  /notes/{id}
DELETE /notes/{id}
POST /notes/{id}/share
PATCH /notes/{id}/pin
GET  /search
GET  /about
GET  /openapi.json
```

---

## Required Annotations Per Controller

```java
// Class level
@Tag(name = "Notes", description = "Notes CRUD operations")

// Method level — example
@Operation(
    summary = "Create a new note",
    security = @SecurityRequirement(name = "BearerAuth")
)
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Note created"),
    @ApiResponse(responseCode = "400", description = "Validation error"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
```

---

## Security Scheme (MUST be present in /openapi.json)

```java
// In OpenApiConfig.java
new SecurityScheme()
    .type(SecurityScheme.Type.HTTP)
    .scheme("bearer")
    .bearerFormat("JWT")
    .name("BearerAuth")
```

---

## Request/Response Schema Rules

- ALL request bodies must have schema defined (via DTO annotations)
- ALL response bodies must have schema defined
- `@Schema(description = "...")` on DTO fields is encouraged
- The spec must show which endpoints require auth (BearerAuth)
- The spec must show correct HTTP status codes per endpoint

---

## Minimum Spec Structure

```json
{
  "openapi": "3.0.1",
  "info": {
    "title": "Notes App API",
    "version": "1.0.0",
    "description": "Multi-user Notes Backend API"
  },
  "components": {
    "securitySchemes": {
      "BearerAuth": {
        "type": "http",
        "scheme": "bearer",
        "bearerFormat": "JWT"
      }
    }
  },
  "paths": {
    "/register": { ... },
    "/login": { ... },
    "/notes": { ... },
    "/notes/{id}": { ... },
    "/notes/{id}/share": { ... },
    "/notes/{id}/pin": { ... },
    "/search": { ... },
    "/about": { ... }
  }
}
```
