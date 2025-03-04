package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String userName;

    @Nationalized
    private String fullName;

    private String password;

    private String email;

    @Nationalized
    private String description;

    private String phone;

    private String userImgUrl;

    private String age;

    private LocalDate birthday;

    /**
     * @param idNumber: can cuoc cong dan
     */
    private String idNumber;

    @Nationalized
    private String job;

    private String apartmentName;

    private String role;

    public UserDTO(String fullName, String email, String description, String phone, String job, String age, LocalDate birthday) {
        this.fullName = fullName;
        this.email = email;
        this.description = description;
        this.phone = phone;
        this.job = job;
        this.age = age;
        this.birthday = birthday;
    }
}
