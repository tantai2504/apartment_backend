package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.FacilityRequestDTO;
import com.example.apartmentmanagement.dto.FacilityResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FacilityService {
    List<FacilityResponseDTO> getFacilities();

    List<FacilityResponseDTO> getRejectedFacilities();

    List<FacilityResponseDTO> getVerifiedFacilities();

    List<FacilityResponseDTO> getUnverifiedFacilities();

    FacilityResponseDTO createFacility(FacilityRequestDTO facilityRequestDTO, List<MultipartFile> imageFiles);

    void deleteFacility(Long id);

    FacilityResponseDTO getFacility(Long id);

    FacilityResponseDTO updateFacility(Long id, FacilityRequestDTO facilityRequestDTO, List<MultipartFile> imageFiles);

    FacilityResponseDTO rejectFacility(Long id, String reason, Long verifiedUserId);

    FacilityResponseDTO verifyFacility(Long facilityId, Long verifiedUserId);

    List<FacilityResponseDTO> getFacilityByUserId(Long userId);
}
