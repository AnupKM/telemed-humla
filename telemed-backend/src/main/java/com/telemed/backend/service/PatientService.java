package com.telemed.backend.service;

import com.telemed.backend.dto.PatientRequest;
import com.telemed.backend.dto.PatientResponse;
import com.telemed.backend.entity.Patient;
import com.telemed.backend.entity.User;
import com.telemed.backend.repository.PatientRepository;
import com.telemed.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;

    public PatientResponse addPatient(PatientRequest request, UUID createdBy) {
        byte[] encryptedEmail = encryptionService.encrypt(request.getEmail());
        byte[] encryptedPhone = encryptionService.encrypt(request.getPhone());
        User creator = userRepository.findById(createdBy)
                .orElseThrow(() -> new RuntimeException("Creator (User) not found"));

        Patient patient = Patient.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .age(request.getAge())
                .heightCm(request.getHeightCm())
                .weightKg(request.getWeightKg())
                .creator(creator)
                .emailEncrypted(encryptedEmail)
                .phoneEncrypted(encryptedPhone)
                .build();

        return mapToResponse(patientRepository.save(patient));
    }

    public PatientResponse updatePatient(UUID id, PatientRequest request, UUID updatedBy) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        User updater = userRepository.findById(updatedBy)
                .orElseThrow(() -> new RuntimeException("Creator (User) not found"));

        byte[] encryptedEmail = encryptionService.encrypt(request.getEmail());
        byte[] encryptedPhone = encryptionService.encrypt(request.getPhone());

        patient.setFirstName(request.getFirstName());
        patient.setMiddleName(request.getMiddleName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setAge(request.getAge());
        patient.setHeightCm(request.getHeightCm());
        patient.setWeightKg(request.getWeightKg());
        patient.setUpdatedBy(updater);
        patient.setEmailEncrypted(encryptedEmail);
        patient.setPhoneEncrypted(encryptedPhone);

        return mapToResponse(patientRepository.save(patient));
    }

    public void softDeletePatient(UUID id, UUID deletedBy) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        User deleter = userRepository.findById(deletedBy)
                .orElseThrow(() -> new RuntimeException("User not found"));

        patient.setDeletedAt(Instant.now());
        patient.setUpdatedBy(deleter);

        patientRepository.save(patient);
    }

    public PatientResponse getPatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return mapToResponse(patient);
    }

    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PatientResponse mapToResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .middleName(p.getMiddleName())
                .lastName(p.getLastName())
                .dateOfBirth(p.getDateOfBirth())
                .email(encryptionService.decrypt(p.getEmailEncrypted()))
                .phone(encryptionService.decrypt(p.getPhoneEncrypted()))
                .age(p.getAge())
                .heightCm(p.getHeightCm())
                .weightKg(p.getWeightKg())
                .createdAt(p.getCreatedAt())
                .build();
    }
}