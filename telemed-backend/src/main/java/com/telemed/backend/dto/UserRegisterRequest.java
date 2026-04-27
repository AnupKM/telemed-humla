package com.telemed.backend.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {

    private String email;
    private String password;
    private String fullName;

}