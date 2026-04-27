package com.telemed.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true, columnDefinition = "citext")
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private Instant accountLockedUntil;

    @Column(name = "last_login_at", columnDefinition = "timestamptz")
    private Instant lastLoginAt;

    @Column(name = "deleted_at", columnDefinition = "timestamptz")
    private Instant deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "timestamptz")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "timestamptz")
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(Instant.now());
    }

    public void incrementFailedLogin() {
        this.failedLoginAttempts++;
    }

    public Set<String> getRoleNames() {
        return userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toUnmodifiableSet());
    }

}
