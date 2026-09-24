package backend.service;

import backend.dto.UserRegisterDTO;
import backend.dto.UserLoginDTO;
import backend.model.User;
import backend.repository.UserRepository;
import backend.security.JwtUtil;
import backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;



@Service
public class UserServiceImpl implements UserService {

    // Initialize SLF4J Logger for secure logging
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void registerUser(UserRegisterDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            // Log attempt without exposing sensitive data
            logger.warn("SECURITY ALERT: Registration attempt with existing email: {}", request.getEmail());
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("SECURITY ALERT: Registration attempt with existing username: {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getEmail(), request.getUsername(), hashedPassword);
        userRepository.save(user);

        logger.info("New user registered successfully: {}", request.getUsername());
    }

    @Override
    public String loginUser(UserLoginDTO request) {
        // SECURITY FIX: Removed insecure System.out.println that was logging plain-text passwords
        // Old vulnerable code: System.out.println("Login request: " + request.getEmailOrUsername() + ", " + request.getPassword());

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmailOrUsername());
        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByUsername(request.getEmailOrUsername());
        }

        if (optionalUser.isEmpty()) {
            // SECURITY FIX: Log failed login attempt WITHOUT logging the password
            logger.warn("SECURITY ALERT: Failed login attempt - User not found for identifier: {} at {}",
                    request.getEmailOrUsername(), LocalDateTime.now());
            throw new RuntimeException("Invalid credentials");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // SECURITY FIX: Log failed password attempt WITHOUT logging the password
            logger.warn("SECURITY ALERT: Failed login attempt - Invalid password for user: {} at {}",
                    user.getUsername(), LocalDateTime.now());
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("User logged in successfully: {}", user.getUsername());
        return jwtUtil.generateToken(user.getUsername());
    }

    @Override
    public User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}