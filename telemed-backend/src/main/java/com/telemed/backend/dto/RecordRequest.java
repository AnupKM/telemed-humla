package com.telemed.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordRequest {

    private UUID id;
    private UUID patientId;
    private String patientFullName;
    private Map<String, String> patientHistory;
    private Integer patientAge;

}
