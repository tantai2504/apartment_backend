package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long userId;

    private String userName;

    @Nationalized
    private String fullName;

    private String email;

    @Nationalized
    private String description;

    private String phone;

    private String userImgUrl;

    private String age;

    private LocalDate birthday;

    public UserResponseDTO(Long userId, String userName, String fullName, String email, String description, String phone, String userImgUrl, String age, LocalDate birthday, String idNumber, String job, String role) {
        this.userId = userId;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.description = description;
        this.phone = phone;
        this.userImgUrl = userImgUrl;
        this.age = age;
        this.birthday = birthday;
        this.idNumber = idNumber;
        this.job = job;
        this.role = role;
    }

    /**
     * @param idNumber: can cuoc cong dan
     */
    private String idNumber;

    @Nationalized
    private String job;

    private List<ApartmentResponseInUserDTO> apartment;

    private String role;
}
