package com.telemed.backend.repository;

import com.telemed.backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    @Query(value = "SELECT * FROM patients", nativeQuery = true)
    List<Patient> findAllIncludingDeleted();

    @Query(value = "SELECT * FROM patients WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<Patient> findAllDeletedOnly();

    List<Patient> findAllByOrderByCreatedAtDesc();
}