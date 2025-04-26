package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.TerminateContractRequestDTO;
import com.example.apartmentmanagement.dto.VerifyUserRequestDTO;
import com.example.apartmentmanagement.dto.VerifyUserResponseDTO;
import com.example.apartmentmanagement.service.VerificationFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/verification")
public class VerificationFormController {

    @Autowired
    private VerificationFormService verificationFormService;

    @GetMapping("/list_contract_owner")
    public ResponseEntity<Object> listContractOwner(@RequestParam String apartmentName) {
        Map<String, Object> response = new HashMap<>();
        List<VerifyUserResponseDTO> contractList = verificationFormService.showAllContract(apartmentName);

        if (!contractList.isEmpty()) {
            response.put("status", HttpStatus.OK.value());
            response.put("data", contractList);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Không có hợp đồng nào");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @PutMapping("/update_verification")
    public ResponseEntity<Object> updateVerification(
            @RequestParam("verificationId") Long verificationId,
            @RequestParam("contractStartDate") String contractStartDateStr,
            @RequestParam(value = "contractEndDate", required = false) String contractEndDateStr,
            @RequestPart("imageFile") List<MultipartFile> imageFiles) {
        LocalDateTime contractStartDate = LocalDateTime.parse(contractStartDateStr, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime contractEndDate = LocalDateTime.parse(contractEndDateStr, DateTimeFormatter.ISO_DATE_TIME);

        Map<String, Object> response = new HashMap<>();
        VerifyUserRequestDTO verifyUserDTO = new VerifyUserRequestDTO(contractStartDate, contractEndDate);
        try {
            VerifyUserResponseDTO verifyUserResponseDTO = verificationFormService.updateVerifyUser(verificationId, verifyUserDTO, imageFiles);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", verifyUserResponseDTO);
            response.put("message", "Cập nhật thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/terminate_contract")
    public ResponseEntity<Object> terminateContract(@RequestBody TerminateContractRequestDTO requestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            verificationFormService.terminateContract(
                    requestDTO.getUserId(),
                    requestDTO.getApartmentId(),
                    requestDTO.getReason()
            );

            response.put("status", HttpStatus.OK.value());
            response.put("message", "Hợp đồng đã được chấm dứt thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
