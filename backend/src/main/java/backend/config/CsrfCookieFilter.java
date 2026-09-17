package backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to ensure the CsrfToken is loaded on every request, which forces
 * Spring Security's CookieCsrfTokenRepository to write the XSRF-TOKEN cookie
 * to the HTTP response for Single Page Applications (SPA).
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            // Calling getToken() causes the deferred token to be resolved and written to the cookie
            csrfToken.getToken();
        }
        filterChain.doFilter(request, response);
    }
}
