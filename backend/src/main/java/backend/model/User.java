package backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    // @JsonIgnore: never let Jackson serialize this into an API response, regardless of which
    // endpoint or nested relationship (Order.getUser(), CartItem.getUser(), etc.) exposes a User
    // object. Found leaking the raw bcrypt hash via GET /api/auth/me and every /api/orders/**
    // response - a single blanket guard here protects every current and future path at once,
    // rather than needing a DTO on each controller individually.
    @JsonIgnore
    @NotBlank
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    // Relationships to Wishlist, Cart, Orders, Reviews (Optional for DTOs)
    //    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //    private Set<Review> reviews;

    // You may also use mapped collections for wishlist, cartItems, orders as needed

    // --- Getters and Setters ---

    // Constructors, default and parameterized
    public User() {}

    public User(String email, String username, String passwordHash) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    //    public Set<Review> getReviews() {
    //        return reviews;
    //    }
}
