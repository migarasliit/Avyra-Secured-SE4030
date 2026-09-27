package backend.dto;

import backend.model.User;

import java.time.LocalDateTime;

/**
 * What /api/auth/me actually returns to the client.
 *
 * Never expose the User entity directly here - Jackson serializes every public
 * getter by default, which previously included getPasswordHash(), leaking the
 * bcrypt hash to any caller (visible in DevTools Network tab / any XHR).
 */
public class UserResponseDTO {
    private final Long id;
    private final String email;
    private final String username;
    private final LocalDateTime registeredAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.registeredAt = user.getRegisteredAt();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
}
