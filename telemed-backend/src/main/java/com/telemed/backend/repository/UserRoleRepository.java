package com.telemed.backend.repository;

import com.telemed.backend.entity.UserRole;
import com.telemed.backend.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    List<UserRole> findByIdUserId(UUID userId);

    void deleteByIdUserIdAndIdRoleId(UUID userId, UUID roleId);

    boolean existsByIdUserIdAndIdRoleId(UUID userId, UUID roleId);

    boolean existsById(UserRoleId userRoleId);
}