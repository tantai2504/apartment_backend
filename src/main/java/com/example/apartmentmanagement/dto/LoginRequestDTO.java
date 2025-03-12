package com.example.apartmentmanagement.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String usernameOrEmail;
    private String password;
}
