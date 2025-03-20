package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddNewResidentRequestDTO {
    private String userName;
    private String password;
    private Long apartmentId;
    private String fullName;
    private String role;
}
