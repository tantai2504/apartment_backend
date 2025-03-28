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
public class    UserResponseDTO {
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

    /**
     * @param idNumber: can cuoc cong dan
     */
    private String idNumber;

    @Nationalized
    private String job;

    private List<ApartmentResponseInUserDTO> apartment;

    private String role;
}
