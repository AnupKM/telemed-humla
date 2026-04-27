package com.telemed.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private UUID userId;
    private String email;
    private String fullName;
    private Set<String> roles;

}