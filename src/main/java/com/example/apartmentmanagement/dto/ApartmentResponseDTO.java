package com.example.apartmentmanagement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApartmentResponseDTO {
    private Long apartmentId;
    private String apartmentName;
    private String householder;
    private int totalNumber;
    private String status;
    private String aptImgUrl;
    private int numberOfBedrooms;
    private int numberOfBathrooms;
    private String note;
    private String direction;
    private String floor;
    private String area;
    private List<String> users;

    public ApartmentResponseDTO(Long apartmentId, String apartmentName, String householder, int totalNumber, String status, String aptImgUrl, int numberOfBedrooms, int numberOfBathrooms, String note, String direction, String floor, String area) {
        this.apartmentId = apartmentId;
        this.apartmentName = apartmentName;
        this.householder = householder;
        this.totalNumber = totalNumber;
        this.status = status;
        this.aptImgUrl = aptImgUrl;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.note = note;
        this.direction = direction;
        this.floor = floor;
        this.area = area;
    }

    public ApartmentResponseDTO(Long apartmentId, String apartmentName, String householder, int totalNumber, String status, String aptImgUrl, int numberOfBedrooms, int numberOfBathrooms, String note, String direction, String floor, String area, List<String> users) {
        this.apartmentId = apartmentId;
        this.apartmentName = apartmentName;
        this.householder = householder;
        this.totalNumber = totalNumber;
        this.status = status;
        this.aptImgUrl = aptImgUrl;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.note = note;
        this.direction = direction;
        this.floor = floor;
        this.area = area;
        this.users = users;
    }
}
