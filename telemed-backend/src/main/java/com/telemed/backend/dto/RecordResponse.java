package com.telemed.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse {

    private UUID id;
    private UUID patientId;
    private String patientFullName;
    private String recordCreatedByFullName;
    private Map<String, String> patientHistory;
    private Integer patientAge;
    private Instant updatedAt;
    private Instant createdAt;

}