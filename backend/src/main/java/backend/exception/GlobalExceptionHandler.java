package backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Application-wide exception handler.
 *
 * Must be a top-level class: Spring's component scanner ignores non-static
 * inner classes (ClassMetadata#isIndependent() returns false for them), so a
 * @ControllerAdvice nested inside a controller is never registered as a bean.
 *
 * Exception details are logged server-side only. The client response never
 * includes ex.getMessage() or a stack trace, matching the
 * server.error.include-message=never / include-stacktrace=never posture in
 * application.properties for unhandled exceptions in general.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // IDOR fix (deleteReview etc.): the caller is authenticated but not authorized to act
    // on this resource. Never echo e.getMessage() - keeps internal identifiers out of the response.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Access denied"));
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ReviewNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Review not found"));
    }

    // Covers UsernameNotFoundException thrown by UserServiceImpl.getAuthenticatedUser()
    // when the caller isn't logged in: Spring Security's default anonymous-authentication
    // filter still populates a principal named "anonymousUser" for unauthenticated requests,
    // so the lookup fails with an AuthenticationException subtype rather than the request
    // being rejected earlier. Without this, that case fell through to the generic 500 handler
    // below for any unauthenticated hit on an endpoint that calls getAuthenticatedUser()
    // (e.g. GET /api/auth/me, /api/wishlist on initial page load before login).
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationError(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication required"));
    }

    // @Valid failures (e.g. blank/malformed fields on register/login) were falling through
    // to the generic 500 handler below instead of a proper 400, since
    // MethodArgumentNotValidException isn't an AuthenticationException. Field-level messages
    // are safe to return here: they describe the caller's own submitted input, not server
    // internals, so this doesn't conflict with the server.error.include-message=never posture
    // for unhandled/internal errors.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation failed", "fields", fieldErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleError(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
