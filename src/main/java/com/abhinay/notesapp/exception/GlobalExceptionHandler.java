package com.abhinay.notesapp.exception;

import com.abhinay.notesapp.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(400).body(ErrorResponse.of(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoteNotFound(NoteNotFoundException ex) {
        return ResponseEntity.status(404).body(ErrorResponse.of(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(ErrorResponse.of(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(409).body(ErrorResponse.of(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(NoteAlreadySharedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyShared(NoteAlreadySharedException ex) {
        return ResponseEntity.status(409).body(ErrorResponse.of(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(NoteAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(NoteAccessDeniedException ex) {
        return ResponseEntity.status(403).body(ErrorResponse.of(403, "Forbidden", ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(401).body(ErrorResponse.of(401, "Unauthorized", ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        int code = ex.getStatusCode().value();
        String error = HttpStatus.valueOf(code).getReasonPhrase();
        String message = ex.getReason() == null ? error : ex.getReason();
        return ResponseEntity.status(code)
                .body(ErrorResponse.of(code, error, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");
        return ResponseEntity.status(400).body(ErrorResponse.of(400, "Bad Request", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "Bad Request", "Malformed JSON request body"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if ("page".equals(ex.getName())) {
            return ResponseEntity.status(400)
                    .body(ErrorResponse.of(400, "Bad Request", "Invalid page parameter"));
        }
        if ("size".equals(ex.getName())) {
            return ResponseEntity.status(400)
                    .body(ErrorResponse.of(400, "Bad Request", "Invalid size parameter"));
        }
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "Bad Request", "Invalid ID format"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "Bad Request", "Required parameter missing: " + ex.getParameterName()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(415)
                .body(ErrorResponse.of(415, "Unsupported Media Type", "Content-Type must be application/json"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex) {
        return ResponseEntity.status(404)
                .body(ErrorResponse.of(404, "Not Found", "Endpoint not found"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Database constraint violation: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(409)
                .body(ErrorResponse.of(409, "Conflict", "Resource conflict"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(ErrorResponse.of(500, "Internal Server Error", "Internal server error"));
    }
}
