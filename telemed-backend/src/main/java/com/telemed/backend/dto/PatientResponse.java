package com.telemed.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PatientResponse {

    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private Instant createdAt;
}