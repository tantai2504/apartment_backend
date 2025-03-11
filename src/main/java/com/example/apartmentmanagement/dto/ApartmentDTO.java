package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentDTO {
    private Long apartmentId;
    private String apartmentName;
    private String householder;
    private int totalNumber;
    private String status;
    private String aptImgUrl;
    private List<String> users;
}
