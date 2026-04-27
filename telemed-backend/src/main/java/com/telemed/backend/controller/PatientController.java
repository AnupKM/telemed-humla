package com.telemed.backend.controller;

import com.telemed.backend.dto.PatientRequest;
import com.telemed.backend.dto.PatientResponse;
import com.telemed.backend.security.SecurityUtils;
import com.telemed.backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('REGISTRAR')")
    public ResponseEntity<PatientResponse> createPatient(
            @RequestBody PatientRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(patientService.addPatient(request, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('REGISTRAR')")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable UUID id,
            @RequestBody PatientRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(patientService.updatePatient(id, request, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REGISTRAR')")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        patientService.softDeletePatient(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('REGISTRAR')")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatient(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('REGISTRAR')")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }
}