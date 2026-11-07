
package com.confirmmyshow.userservice.exception;

import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.confirmmyshow.userservice.constan.Constants;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Common error response
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            String code,
            String message,
            Object details) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", Instant.now());
        error.put("status", status.value());
        error.put("code", code);
        error.put("message", message);

        if (details != null) {
            error.put("details", details);
        }

        return ResponseEntity.status(status).body(error);
    }

    // 400 - Request validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return buildErrorResponse(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","Request validation failed",errors);
    }

    // 400 - Constraint validation errors
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","Request validation failed",ex.getMessage());
    }

    // 400 - Invalid JSON request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(HttpMessageNotReadableException ex) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","Invalid request body",null);
    }

    // 409 - Duplicate email
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(EmailAlreadyRegisteredException ex) {

        return buildErrorResponse(HttpStatus.CONFLICT,"EMAIL_ALREADY_REGISTERED",ex.getMessage(),null);
    }

    // 401 - Invalid credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return buildErrorResponse(HttpStatus.UNAUTHORIZED,"INVALID_CREDENTIALS","Invalid email or password",null);
    }

    // 401 - Invalid refresh token
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "INVALID_REFRESH_TOKEN",
                "Invalid or expired refresh token",
                null
        );
    }

    // 423 - Account locked
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountLocked(
            AccountLockedException ex) {

        return buildErrorResponse(
                HttpStatus.valueOf(423),
                "ACCOUNT_LOCKED",
                "Account is temporarily locked",
                null
        );
    }

    // 404 - Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                ex.getMessage(),
                null
        );
    }

    // 409 - Database constraint violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "DATA_CONFLICT",
                "The request conflicts with existing data",
                null
        );
    }

    // 500 - Unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex) {

        // Log the exception internally; do not expose stack traces.
        ex.printStackTrace();

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_SERVER_ERROR","An unexpected error occurred",null);
    }
    
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatusTransition(
    		InvalidStatusTransitionException ex) {

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,Constants.FAILED,ex.getMessage(),ex.getMessage());
    }
}
