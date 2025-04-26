package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.VerifyUserRequestDTO;
import com.example.apartmentmanagement.dto.VerifyUserResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VerificationFormService {
    List<VerifyUserResponseDTO> showAllContract(String apartmentName);
    VerifyUserResponseDTO updateVerifyUser(Long verificationId, VerifyUserRequestDTO verifyUserDTO, List<MultipartFile> imageFile);
    void terminateContract(Long userId, Long apartmentId, String reason);
}
