package com.telemed.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PatientRequest {

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Integer age;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
}