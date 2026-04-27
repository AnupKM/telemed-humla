package com.telemed.backend.repository;

import com.telemed.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE lower(u.email) = lower(:email) AND u.deletedAt IS NULL")
    Optional<User> findActiveByEmailIgnoreCase(@Param("email") String email);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<User> findAllIncludingDeleted();

    @Query(value = "SELECT * FROM users WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<User> findAllDeletedOnly();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findActiveByEmailWithRoles(@Param("email") String email);
}