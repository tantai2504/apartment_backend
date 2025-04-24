package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.FacilityRequestDTO;
import com.example.apartmentmanagement.dto.FacilityResponseDTO;
import com.example.apartmentmanagement.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/facility")
public class FacilityController {
    @Autowired
    private FacilityService facilityService;

    @GetMapping("/getAll")
    public ResponseEntity<Object> showAllFacilities() {
        List<FacilityResponseDTO> facilityResponseDTOS = facilityService.getFacilities();
        Map<String, Object> response = new HashMap<>();
        if (facilityResponseDTOS.isEmpty()) {
            response.put("message", "Không có bài đăng dịch vụ nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", facilityResponseDTOS);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFacilityByUser/{userId}")
    public ResponseEntity<Object> getFacilityByUserId(@PathVariable("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        List<FacilityResponseDTO> facilityList = facilityService.getFacilityByUserId(userId);

        if (facilityList.isEmpty()) {
            response.put("message", "Không có bài đăng dịch vụ nào cho userId: " + userId);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        response.put("data", facilityList);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_verified")
    public ResponseEntity<Object> showAllVerifiedFacilities() {
        List<FacilityResponseDTO> facilityResponseDTOS = facilityService.getVerifiedFacilities();
        Map<String, Object> response = new HashMap<>();
        if (facilityResponseDTOS.isEmpty()) {
            response.put("message", "Không có bài đăng dịch vụ nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", facilityResponseDTOS);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_unverified")
    public ResponseEntity<Object> showAllUnverifiedFacilities() {
        List<FacilityResponseDTO> facilityResponseDTOS = facilityService.getUnverifiedFacilities();
        Map<String, Object> response = new HashMap<>();
        if (facilityResponseDTOS.isEmpty()) {
            response.put("message", "Không có bài đăng dịch vụ nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", facilityResponseDTOS);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_rejected")
    public ResponseEntity<Object> showAllRejectedFacilities() {
        List<FacilityResponseDTO> facilityResponseDTOS = facilityService.getRejectedFacilities();
        Map<String, Object> response = new HashMap<>();
        if (facilityResponseDTOS.isEmpty()) {
            response.put("message", "Không có bài đăng dịch vụ nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", facilityResponseDTOS);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/view_facility_post/{facilityId}")
    public ResponseEntity<Object> viewFacility(@PathVariable("facilityId") Long facilityId){
        Map<String, Object> response = new HashMap<>();
        FacilityResponseDTO facilityResponseDTO = facilityService.getFacility(facilityId);
        if (facilityResponseDTO == null) {
            response.put("message", "Không có bài đăng dịch vụ nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", facilityResponseDTO);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createFacility(@RequestParam ("userId") Long userId,
                                                 @RequestParam ("facilityPostContent") String facilityPostContent,
                                                 @RequestParam ("facilityHeader") String facilityHeader,
                                                 @RequestParam ("file") List<MultipartFile> file) {
        FacilityRequestDTO facilityRequestDTO = new FacilityRequestDTO(userId, facilityPostContent, facilityHeader);
        Map<String, Object> response = new HashMap<>();
        try {
            FacilityResponseDTO facilityResponseDTO = facilityService.createFacility(facilityRequestDTO, file);
            response.put("message", "Khởi tạo bài post dịch vụ thành công, hãy chờ xét duyệt");
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", facilityResponseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/verified/{facilityId}")
    public ResponseEntity<Object> verifiedFacilityPost(@PathVariable ("facilityId") Long facilityId,
                                                       @RequestBody Map<String, Object> verifiedFacilityPost) {
        Map<String, Object> response = new HashMap<>();
        Long verifiedUserId = Long.parseLong(verifiedFacilityPost.get("verifiedUserId").toString());
        try {
            FacilityResponseDTO facilityResponseDTO = facilityService.verifyFacility(facilityId, verifiedUserId);
            response.put("message", "Duyệt bài thành công");
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", facilityResponseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/rejected/{facilityId}")
    public ResponseEntity<Object> rejectFacilityPost(@PathVariable ("facilityId") Long facilityId,
                                                     @RequestBody Map<String, Object> rejectFacilityPost) {
        Map<String, Object> response = new HashMap<>();
        Long verifiedUserId = Long.parseLong(rejectFacilityPost.get("verifiedUserId").toString());
        String reason = rejectFacilityPost.get("reason").toString();
        try {
            FacilityResponseDTO facilityResponseDTO = facilityService.rejectFacility(facilityId, reason, verifiedUserId);
            response.put("message", "Reject thành công");
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", facilityResponseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{facilityId}")
    public ResponseEntity<Object> deleteFacilityPost(@PathVariable ("facilityId") Long facilityId) {
        facilityService.deleteFacility(facilityId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{facilityId}")
    public ResponseEntity<Object> updateFacilityPost(@PathVariable ("facilityId") Long facilityId,
                                                     @RequestParam ("userId") Long userId,
                                                     @RequestParam ("facilityHeader") String facilityHeader,
                                                     @RequestParam ("facilityPostContent") String facilityPostContent,
                                                     @RequestParam ("file") List<MultipartFile> file) {
        Map<String, Object> response = new HashMap<>();
        FacilityRequestDTO facilityRequestDTO = new FacilityRequestDTO(userId, facilityPostContent, facilityHeader);
        try {
            FacilityResponseDTO facilityResponseDTO = facilityService.updateFacility(facilityId, facilityRequestDTO, file);
            response.put("message", "Update bài post dịch vụ thành công, hãy chờ xét duyệt");
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", facilityResponseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
