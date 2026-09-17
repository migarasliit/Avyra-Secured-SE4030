package backend.security;

import backend.model.User;
import backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles successful OAuth2 authentication with Google.
 * Maps or registers the Google user, generates an internal JWT,
 * and attaches it as an HttpOnly, Secure, SameSite=Strict cookie before
 * redirecting back to the React frontend.
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.cookie.secure:true}")
    private boolean isSecureCookie;

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:5173}")
    private String redirectUri;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new IllegalArgumentException("Email attribute not provided by Google OAuth2 provider");
        }

        // 1. Find existing user or auto-provision new user on first Google login
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            String name = oAuth2User.getAttribute("name");
            String baseUsername = (name != null && !name.isBlank())
                    ? name.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase()
                    : email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();

            if (baseUsername.length() < 3) {
                baseUsername = "user_" + UUID.randomUUID().toString().substring(0, 6);
            }

            String username = baseUsername;
            int counter = 1;
            while (userRepository.findByUsername(username).isPresent()) {
                username = baseUsername + counter++;
            }

            // Create user with a secure random unguessable password
            String randomPassword = UUID.randomUUID().toString();
            user = new User(email, username, passwordEncoder.encode(randomPassword));
            user = userRepository.save(user);
        }

        // 2. Generate internal JWT for the authenticated user
        String jwtToken = jwtUtil.generateToken(user.getUsername());

        // 3. Attach JWT into HttpOnly, Secure, SameSite=Strict cookie
        ResponseCookie cookie = ResponseCookie.from("jwtToken", jwtToken)
                .httpOnly(true)
                .secure(isSecureCookie)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 4. Redirect browser back to React SPA
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
