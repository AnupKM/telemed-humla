package com.telemed.backend.service;

import com.telemed.backend.entity.User;
import com.telemed.backend.exception.AuthenticationException;
import com.telemed.backend.exception.UserAlreadyExistsException;
import com.telemed.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String email, String password, String name) {

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User newUser = User.builder()
                .email(email)
                .passwordHash(encodedPassword)
                .fullName(name)
                .isActive(true)
                .build();

        return userRepository.save(newUser);
    }

    public User authenticate(String email, String password) {

        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (user.isAccountLocked()) {
            log.warn("Login attempt on locked account: {}", email);
            throw new AuthenticationException("Account is temporarily locked, try again later");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }

        resetFailedAttempts(user);

        return user;
    }

    public Optional<User> findActiveUserByEmail(String email) {
        return userRepository.findActiveByEmail(email);
    }

    private void handleFailedLogin(User user) {
        user.incrementFailedLogin();

        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLockedUntil(Instant.now().plusSeconds(LOCK_DURATION_MINUTES * 60));
            log.warn("Account locked due to too many failed attempts: {}", user.getEmail());
        }

        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0 || user.getAccountLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
        } else {
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
        }
    }
}