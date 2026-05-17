# OpenAPI + About Module PRD — Agent 4

## Scope
Create: OpenApiController, AboutController, OpenApiConfig.
DO NOT touch: auth, notes, sharing logic.

---

## GLOBAL ENGINEERING RULES APPLY (see master_prd.md)

---

## GET /openapi.json
```
Returns OpenAPI 3.0 specification JSON.
Use SpringDoc: springdoc-openapi-starter-webmvc-ui
Config: disable swagger-ui (not needed), only expose /openapi.json
Return the spec as-is from SpringDoc (or manually build it).
Must document ALL endpoints with request/response schemas.
```

## GET /about
```json
{
  "name": "Abhinay Yendoti",
  "email": "your-email@example.com",
  "my_features": {
    "Note Pinning": "Users can pin important notes so they always appear at the top of the notes list. Implemented PATCH /notes/{id}/pin endpoint. Chosen because it mirrors real-world note apps like Google Keep and demonstrates understanding of user-centric feature design.",
    "Full-Text Search": "GET /search?q=keyword searches notes by title and content. Returns notes the user owns or has access to. Chosen to demonstrate database query design and real-world utility."
  }
}
```

## config/OpenApiConfig.java
```
@Bean OpenAPI customOpenAPI() — set title, version, description
Add JWT security scheme (Bearer token)
Apply security requirement globally to protected endpoints
```

## controller/AboutController.java
```
@RestController
GET /about → 200 with hardcoded JSON about info
Return as Map or dedicated AboutResponse record
```

## controller/OpenApiController.java
Let SpringDoc auto-expose /openapi.json OR manually return the spec.
Simplest: configure SpringDoc to use path /openapi.json in application.yml.
