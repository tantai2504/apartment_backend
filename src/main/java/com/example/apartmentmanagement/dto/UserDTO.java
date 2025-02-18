package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String userName;

    private String email;

    private String description;

    private String phone;

    private String userImgUrl;

    private String role;
}
