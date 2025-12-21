package com.shout.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler - Improved
 * Provides clear, user-friendly error messages to frontend
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("❌ Resource not found: {}", ex.getMessage());
        
        Map<String, Object> error = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "Resource Not Found",
            ex.getMessage(),
            request
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        log.error("🔐 Unauthorized access: {}", ex.getMessage());
        
        Map<String, Object> error = buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Unauthorized",
            "Authentication required. Please log in.",
            request
        );
        
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        log.error("⚠️ Bad request: {}", ex.getMessage());
        
        Map<String, Object> error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage(),
            request
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("✏️ Validation error: {}", ex.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Validation Failed");
        error.put("message", "Please check your input and try again");
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> 
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        error.put("errors", fieldErrors);
        error.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(
            RateLimitExceededException ex, WebRequest request) {
        log.warn("⏱️ Rate limit exceeded: {}", ex.getMessage());
        
        Map<String, Object> error = buildErrorResponse(
            HttpStatus.TOO_MANY_REQUESTS,
            "Rate Limit Exceeded",
            ex.getMessage(),
            request
        );
        
        error.put("remainingRequests", ex.getRemainingRequests());
        error.put("resetTime", ex.getResetTime());
        
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(SubscriptionException.class)
    public ResponseEntity<Map<String, Object>> handleSubscriptionException(
            SubscriptionException ex, WebRequest request) {
        log.error("💳 Subscription error: {}", ex.getMessage());
        
        Map<String, Object> error = buildErrorResponse(
            HttpStatus.PAYMENT_REQUIRED,
            "Subscription Required",
            ex.getMessage() + ". Please upgrade to PRO to access this feature.",
            request
        );
        
        error.put("upgradeUrl", "/payments");
        
        return new ResponseEntity<>(error, HttpStatus.PAYMENT_REQUIRED);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentFailed(
            PaymentFailedException ex, WebRequest request) {
        log.error("💰 Payment failed: {}", ex.getMessage());
        
        Map<String, Object> error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Payment Failed",
            ex.getMessage() + ". Please try again or contact support.",
            request
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {
        log.warn("🔁 Duplicate resource: {}", ex.getMessage());
        
        Map<String, Object> error = buildErrorResponse(
            HttpStatus.CONFLICT,
            "Duplicate Resource",
            ex.getMessage(),
            request
        );
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("💥 Internal server error", ex);
        
        Map<String, Object> error = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request
        );
        
        // Include stack trace in development mode only
        if (log.isDebugEnabled()) {
            error.put("trace", ex.getStackTrace()[0].toString());
        }
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Helper method to build consistent error responses
     */
    private Map<String, Object> buildErrorResponse(
            HttpStatus status, 
            String error, 
            String message, 
            WebRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return response;
    }
}
