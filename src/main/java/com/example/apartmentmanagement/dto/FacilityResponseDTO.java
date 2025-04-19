package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FacilityResponseDTO {
    private String userName;
    private String userPhone;
    private String userEmail;
    private Long facilityId;
    private String facilityPostContent;
    private String facilityHeader;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime verifiedAt;
    private String reason;
    private String verifiedUserName;
    private List<String> images;

    public FacilityResponseDTO(String userName, String userPhone, String userEmail, Long facilityId, String facilityPostContent, String facilityHeader, String status, LocalDateTime createdAt, List<String> images) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.facilityId = facilityId;
        this.facilityPostContent = facilityPostContent;
        this.facilityHeader = facilityHeader;
        this.status = status;
        this.createdAt = createdAt;
        this.images = images;
    }

    public FacilityResponseDTO(String userName, String userPhone, String userEmail, Long facilityId, String facilityPostContent, String facilityHeader, String status, LocalDateTime createdAt, LocalDateTime verifiedAt, List<String> images, String verifiedUserName) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.facilityId = facilityId;
        this.facilityPostContent = facilityPostContent;
        this.facilityHeader = facilityHeader;
        this.status = status;
        this.createdAt = createdAt;
        this.verifiedAt = verifiedAt;
        this.images = images;
        this.verifiedUserName = verifiedUserName;
    }
}
