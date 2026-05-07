package com.telemed.backend.service;

import com.telemed.backend.dto.NameParts;
import com.telemed.backend.dto.RecordRequest;
import com.telemed.backend.dto.RecordResponse;
import com.telemed.backend.entity.MedicalRecord;
import com.telemed.backend.entity.Patient;
import com.telemed.backend.entity.User;
import com.telemed.backend.repository.PatientRepository;
import com.telemed.backend.repository.RecordRepository;
import com.telemed.backend.repository.UserRepository;
import com.telemed.backend.util.NameUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService {

    private final RecordRepository recordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final MedicalRecordPdfService pdfService;

    public List<RecordResponse> getRecordsByPatientId(UUID patientId) {
        return recordRepository.findAllByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RecordResponse getRecordById(UUID id) {
        return recordRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));
    }


    public RecordResponse addRecord(RecordRequest request, UUID creatorId) {

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient;

        if (request.getPatientId() != null) {
            patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
        } else {
            patient = createNewPatient(request, creator);
        }

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .creator(creator)
                .patientHistory(request.getPatientHistory())
                .build();

        MedicalRecord savedRecord = recordRepository.save(record);

        return mapToResponse(savedRecord);
    }

    public void deleteRecord(UUID recordId, UUID currentUserId) {

        MedicalRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        checkPermission(record, currentUserId);

        record.setDeletedAt(Instant.now());
        recordRepository.save(record);
    }

    public RecordResponse updateRecord(RecordRequest request, UUID currentUserId) {

        MedicalRecord record = recordRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));

        checkPermission(record, currentUserId);

        if (request.getPatientHistory() != null) {
            record.setPatientHistory(request.getPatientHistory());
        }

        return mapToResponse(recordRepository.save(record));
    }

    public byte[] generatePdfByRecordId(UUID recordId){

        MedicalRecord record = recordRepository.findById(recordId).orElseThrow(() -> new RuntimeException("Record not found"));
        return pdfService.generateSingleMedicalRecordPdf(record);

    }

    private RecordResponse mapToResponse(MedicalRecord mRecord) {
        String pName = mRecord.getPatient() != null ?
                mRecord.getPatient().getFirstName() + " " + mRecord.getPatient().getLastName() : "Unknown Patient";

        return RecordResponse.builder()
                .id(mRecord.getId())
                .patientId(mRecord.getPatient().getId())
                .patientFullName(pName)
                .recordCreatedByFullName(mRecord.getCreator().getFullName())
                .patientHistory(mRecord.getPatientHistory())
                .updatedAt(mRecord.getUpdatedAt())
                .createdAt(mRecord.getCreatedAt())
                .build();
    }

    private Patient createNewPatient(RecordRequest request, User creator) {
        NameParts nameParts = NameUtil.parseFullname(request.getPatientFullName());

        Patient patient = new Patient();
        patient.setFirstName(nameParts.getFirstName());
        patient.setMiddleName(nameParts.getMiddleName());
        patient.setLastName(nameParts.getLastName());
        patient.setAge(request.getPatientAge());
        patient.setCreator(creator);
        return patientRepository.save(patient);
    }

    private void checkPermission(MedicalRecord record, UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isCreator = record.getCreator().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRoleNames().contains("ROLE_ADMIN");

        if (!isCreator && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to modify this record.");
        }
    }
}