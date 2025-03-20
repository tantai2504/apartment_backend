package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApartmentDTO {
    private Long apartmentId;
    private String apartmentName;
    private String householder;
    private int totalNumber;
    private String status;
    private String aptImgUrl;
    private int numberOfBedrooms;
    private int numberOfBathrooms;
    private String note;
    private List<String> users;

    public ApartmentDTO(Long apartmentId, String apartmentName, String householder, int totalNumber, String status, String aptImgUrl, int numberOfBedrooms, int numberOfBathrooms, String note) {
        this.apartmentId = apartmentId;
        this.apartmentName = apartmentName;
        this.householder = householder;
        this.totalNumber = totalNumber;
        this.status = status;
        this.aptImgUrl = aptImgUrl;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.note = note;
    }

    public ApartmentDTO(Long apartmentId, String apartmentName, String householder, int totalNumber, String status, String aptImgUrl, int numberOfBedrooms, int numberOfBathrooms, String note, List<String> users) {
        this.apartmentId = apartmentId;
        this.apartmentName = apartmentName;
        this.householder = householder;
        this.totalNumber = totalNumber;
        this.status = status;
        this.aptImgUrl = aptImgUrl;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.note = note;
        this.users = users;
    }
}
