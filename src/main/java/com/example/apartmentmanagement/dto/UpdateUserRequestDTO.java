package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDTO {
    private Long userId;
    private String userImgUrl;
    private String fullName;
    private String phone;
    private LocalDate birthday;
    private String description;
    private String job;

    @Override
    public String toString() {
        return "UpdateUserRequestDTO{" +
                "userId=" + userId +
                ", userImgUrl='" + userImgUrl + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", birthday=" + birthday +
                ", description='" + description + '\'' +
                ", job='" + job + '\'' +
                '}';
    }
}
