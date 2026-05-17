# Exception Handling Module PRD — Agent 5

## Scope
Create ALL custom exceptions and GlobalExceptionHandler.
This module must be set up BEFORE other agents start coding.

---

## GLOBAL ENGINEERING RULES APPLY (see master_prd.md)

---

## Custom Exceptions to Create

```java
// All extend RuntimeException
NoteNotFoundException(String message)         → maps to 404
UserNotFoundException(String message)         → maps to 404
EmailAlreadyExistsException(String message)   → maps to 409
NoteAlreadySharedException(String message)    → maps to 409
AccessDeniedException(String message)         → maps to 403
InvalidTokenException(String message)         → maps to 401
```

## dto/response/ErrorResponse.java
```java
int status
String error
String message
LocalDateTime timestamp
```

## exception/GlobalExceptionHandler.java (MAX 200 lines)
```
@RestControllerAdvice

Handle:
  NoteNotFoundException → 404
  UserNotFoundException → 404
  EmailAlreadyExistsException → 409
  NoteAlreadySharedException → 409
  AccessDeniedException → 403
  InvalidTokenException → 401
  MethodArgumentNotValidException → 400 (extract first field error message)
  HttpMessageNotReadableException → 400 "Malformed JSON request body"
  MethodArgumentTypeMismatchException → 400 "Invalid UUID format"
  MissingServletRequestParameterException → 400 "Required parameter missing: {name}"
  HttpMediaTypeNotSupportedException → 415 "Content-Type must be application/json"
  NoHandlerFoundException → 404 "Endpoint not found"
  Exception (generic fallback) → 500 "Internal server error"

RULES:
  - NEVER expose stack traces in response
  - NEVER expose internal exception messages for 500 errors
  - Always return ErrorResponse JSON
  - Log the actual error server-side (use @Slf4j)
```

## Validation Error Format
For @Valid failures, return the FIRST field error only:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Password must be at least 8 characters",
  "timestamp": "2025-01-01T00:00:00"
}
```
