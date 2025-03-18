package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRegisterRequestDTO {
    private String userName;
    private String password;
    private String re_password;
    private String email;
    private String phone;
}
