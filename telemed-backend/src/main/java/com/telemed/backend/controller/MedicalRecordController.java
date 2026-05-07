package com.telemed.backend.controller;

import com.telemed.backend.dto.RecordRequest;
import com.telemed.backend.dto.RecordResponse;
import com.telemed.backend.security.SecurityUtils;
import com.telemed.backend.service.MedicalRecordService;
import com.telemed.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RecordResponse> getMedicalRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(medicalRecordService.getRecordById(id));
    }

    @GetMapping("/all/{patientId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<RecordResponse>> getMedicalRecordsByPatientId(@PathVariable UUID patientId) {
        List<RecordResponse> records = medicalRecordService.getRecordsByPatientId(patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{recordId}/pdf")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<byte[]> downloadPdfByRecordId(@PathVariable UUID recordId) {

        byte[] pdf = medicalRecordService.generatePdfByRecordId(recordId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patient_record.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RecordResponse> createPatient(
            @RequestBody RecordRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(medicalRecordService.addRecord(request, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RecordResponse> updatePatient(
            @PathVariable UUID id,
            @RequestBody RecordRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(medicalRecordService.updateRecord(request, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        medicalRecordService.deleteRecord(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

}
