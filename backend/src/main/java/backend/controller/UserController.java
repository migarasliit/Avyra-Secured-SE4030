package backend.controller;

import backend.dto.UserRegisterDTO;
import backend.dto.UserLoginDTO;
import backend.model.User;
import backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @Value("${app.security.cookie.secure:true}")
    private boolean isSecureCookie;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register Endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO request) {
        userService.registerUser(request); // Handles hashing, uniqueness, exceptions
        return ResponseEntity.ok(Map.of("message", "Registration successful!"));
    }

    // Login Endpoint: Attaches JWT into HttpOnly, Secure, SameSite=Strict cookie
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginDTO request, HttpServletResponse response) {
        String jwtToken = userService.loginUser(request); // Returns JWT on valid login

        ResponseCookie cookie = ResponseCookie.from("jwtToken", jwtToken)
                .httpOnly(true)
                .secure(isSecureCookie)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    // Logout Endpoint: Clears HttpOnly cookie
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(true)
                .secure(isSecureCookie)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // Profile Endpoint (Protected, returns user info)
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        User user = userService.getAuthenticatedUser(); // Requires JWT filter to extract principal
        return ResponseEntity.ok(user);
    }

    // CSRF Token Endpoint to initialize/retrieve CSRF token for SPA
    @GetMapping("/csrf")
    public ResponseEntity<?> getCsrfToken(org.springframework.security.web.csrf.CsrfToken token) {
        return ResponseEntity.ok(Map.of(
                "token", token != null ? token.getToken() : "",
                "headerName", token != null ? token.getHeaderName() : "X-XSRF-TOKEN"
        ));
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleError(Exception ex) {
            ex.printStackTrace(); // Logs to console
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

}

