package backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        // Do NOT echo e.getMessage() — prevents information leakage
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied");
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ReviewNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Review not found");
    }
}