# Deployment Module PRD — Agent 4

## Render.com Deployment

### application.yml requirements
```yaml
server:
  port: ${PORT:8080}  # Render injects PORT env variable

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate.dialect: org.hibernate.dialect.PostgreSQLDialect
  main:
    banner-mode: off

springdoc:
  api-docs:
    path: /openapi.json
  swagger-ui:
    enabled: false

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION}

logging:
  level:
    root: INFO
    com.abhinay.notesapp: DEBUG
```

### render.yaml (for auto-deploy)
```yaml
services:
  - type: web
    name: notes-app
    env: java
    buildCommand: ./mvnw clean package -DskipTests
    startCommand: java -jar target/*.jar
    envVars:
      - key: DB_URL
        sync: false
      - key: DB_USERNAME
        sync: false
      - key: DB_PASSWORD
        sync: false
      - key: JWT_SECRET
        sync: false
      - key: JWT_EXPIRATION
        value: 86400000
```

### Dockerfile (stretch goal)
```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Checklist before deploy
- [ ] .env in .gitignore
- [ ] All secrets via env variables
- [ ] PORT env var used (not hardcoded 8080)
- [ ] DB connection tested
- [ ] JWT working locally
- [ ] All endpoints return correct status codes
- [ ] Automated test compatible (no auth on /about, /openapi.json)
