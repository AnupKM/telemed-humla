package com.telemed.backend.service;

import com.telemed.backend.entity.RefreshToken;
import com.telemed.backend.entity.User;
import com.telemed.backend.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMillis;

    @Transactional
    public String createRefreshToken(User user) {

        refreshTokenRepository.revokeAllByUserId(user.getId());

        String rawToken = UUID.randomUUID().toString() + UUID.randomUUID();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hash(rawToken))
                .expiresAt(Instant.now().plusMillis(refreshExpirationMillis))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public User validateAndRotate(String rawToken) {

        String tokenHash = hash(rawToken);

        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!existing.isValid()) {
            refreshTokenRepository.revokeAllByUserId(existing.getUser().getId());
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        User user = existing.getUser();

        existing.setRevoked(true);
        existing.setDeletedAt(Instant.now());
        refreshTokenRepository.save(existing);

        return user;
    }

    @Transactional
    public void revokeAllTokens(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        refreshTokenRepository.softDeleteAllByUserId(userId, Instant.now());
    }

    private String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}